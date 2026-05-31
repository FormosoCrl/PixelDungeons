import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.hashers import make_password, check_password
from .models import Usuario, Sala, Hero, Item, GameMap, InventoryEntry


def _body(request):
    return json.loads(request.body)


def xp_to_next(level):
    """XP necesaria para pasar de 'level' al siguiente. Curva: 50*n*(n+1)."""
    return 50 * level * (level + 1)


# Crecimiento de stats por nivel según la CLASE (peso principal).
CLASS_GROWTH = {
    'Guerrero':  {'max_hp': 8,  'strength': 3, 'dex': 1, 'defence': 2, 'mana': 0},
    'Arquero':   {'max_hp': 5,  'strength': 2, 'dex': 3, 'defence': 1, 'mana': 1},
    'Mago':      {'max_hp': 3,  'strength': 0, 'dex': 1, 'defence': 1, 'mana': 4},
    'Berserker': {'max_hp': 10, 'strength': 4, 'dex': 1, 'defence': 0, 'mana': 0},
    'Pícaro':    {'max_hp': 5,  'strength': 2, 'dex': 3, 'defence': 1, 'mana': 1},
    'Clérigo':   {'max_hp': 6,  'strength': 1, 'dex': 1, 'defence': 2, 'mana': 3},
}

# Crecimiento de stats por nivel según la RAZA (modificador menor).
RACE_GROWTH = {
    'Humano':  {'max_hp': 2, 'strength': 1, 'dex': 1, 'defence': 1, 'mana': 1},
    'Elfo':    {'max_hp': 0, 'strength': 0, 'dex': 2, 'defence': 0, 'mana': 2},
    'Enano':   {'max_hp': 4, 'strength': 1, 'dex': 0, 'defence': 2, 'mana': 0},
    'Orco':    {'max_hp': 3, 'strength': 2, 'dex': 0, 'defence': 1, 'mana': 0},
    'Mediano': {'max_hp': 1, 'strength': 0, 'dex': 2, 'defence': 1, 'mana': 0},
}


def apply_level_up(h):
    """Aplica el crecimiento de stats de UN nivel según clase + raza del héroe.
    El HP máximo sube y el HP actual sube en la misma cantidad."""
    cls = CLASS_GROWTH.get(h.hero_class, {})
    rac = RACE_GROWTH.get(h.race, {})
    hp_gain = cls.get('max_hp', 0) + rac.get('max_hp', 0)
    h.max_hp += hp_gain
    h.hp += hp_gain
    h.strength += cls.get('strength', 0) + rac.get('strength', 0)
    h.dex += cls.get('dex', 0) + rac.get('dex', 0)
    h.defence += cls.get('defence', 0) + rac.get('defence', 0)
    h.mana += cls.get('mana', 0) + rac.get('mana', 0)


def _stat_field(stat_key):
    """Mapea la clave del bonus_stat al nombre real del campo en Hero."""
    return {
        'str': 'strength',
        'dex': 'dex',
        'def': 'defence',
        'mana': 'mana',
        'hp': 'max_hp',
    }.get(stat_key)


def _apply_item_bonus(hero, item, sign=1):
    """Aplica (sign=+1) o quita (sign=-1) el bonus de un item al héroe.
    Es simétrico: equipar y desequipar se cancelan exactamente.
    Para HP: ajusta max_hp y hp en la misma cantidad, con clamps."""
    field = _stat_field(item.bonus_stat)
    if field is None or item.bonus_value == 0:
        return
    delta = sign * item.bonus_value
    if field == 'max_hp':
        hero.max_hp = max(1, hero.max_hp + delta)
        hero.hp = max(0, min(hero.hp + delta, hero.max_hp))
    else:
        new_val = max(0, getattr(hero, field) + delta)
        setattr(hero, field, new_val)


def apply_level_down(h):
    """Inverso de apply_level_up: resta el crecimiento de UN nivel.
    Stats con clamp a 0, max_hp con clamp a 1, y hp recortado al nuevo max."""
    cls = CLASS_GROWTH.get(h.hero_class, {})
    rac = RACE_GROWTH.get(h.race, {})
    hp_loss = cls.get('max_hp', 0) + rac.get('max_hp', 0)
    h.max_hp = max(1, h.max_hp - hp_loss)
    h.hp = max(0, h.hp - hp_loss)
    h.hp = min(h.hp, h.max_hp)
    h.strength = max(0, h.strength - cls.get('strength', 0) - rac.get('strength', 0))
    h.dex = max(0, h.dex - cls.get('dex', 0) - rac.get('dex', 0))
    h.defence = max(0, h.defence - cls.get('defence', 0) - rac.get('defence', 0))
    h.mana = max(0, h.mana - cls.get('mana', 0) - rac.get('mana', 0))


@csrf_exempt
def registro(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Método no permitido'}, status=405)
    data = _body(request)
    username = data.get('username', '').strip()
    password = data.get('password', '').strip()
    if not username or not password:
        return JsonResponse({'error': 'Faltan campos obligatorios'}, status=400)
    if Usuario.objects.filter(username=username).exists():
        return JsonResponse({'error': 'Usuario ya existe'}, status=400)
    u = Usuario.objects.create(
        username=username,
        password=make_password(password)
    )
    return JsonResponse({'id': u.id, 'username': u.username}, status=201)


@csrf_exempt
def login(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Método no permitido'}, status=405)
    data = _body(request)
    try:
        u = Usuario.objects.get(username=data['username'])
    except Usuario.DoesNotExist:
        return JsonResponse({'error': 'Credenciales incorrectas'}, status=401)
    if not check_password(data['password'], u.password):
        return JsonResponse({'error': 'Credenciales incorrectas'}, status=401)
    return JsonResponse({'id': u.id, 'username': u.username})


@csrf_exempt
def salas(request):
    if request.method == 'GET':
        codigo = request.GET.get('codigo')
        if codigo:
            try:
                s = Sala.objects.get(codigo=codigo)
                return JsonResponse({'id': s.id, 'codigo': s.codigo, 'nombre': s.nombre, 'master_id': s.master_id})
            except Sala.DoesNotExist:
                return JsonResponse({'error': 'Sala no encontrada'}, status=404)
        qs = list(Sala.objects.values('id', 'codigo', 'nombre', 'master_id'))
        return JsonResponse(qs, safe=False)
    if request.method == 'POST':
        data = _body(request)
        if not data.get('codigo') or not data.get('nombre') or not data.get('master_id'):
            return JsonResponse({'error': 'Faltan campos obligatorios'}, status=400)
        s = Sala.objects.create(
            codigo=data['codigo'],
            nombre=data['nombre'],
            master_id=data['master_id']
        )
        return JsonResponse({'id': s.id, 'codigo': s.codigo, 'nombre': s.nombre}, status=201)
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def sala_detalle(request, sala_id):
    try:
        s = Sala.objects.get(id=sala_id)
    except Sala.DoesNotExist:
        return JsonResponse({'error': 'Sala no encontrada'}, status=404)
    if request.method == 'DELETE':
        s.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def heroes(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Método no permitido'}, status=405)
    data = _body(request)
    required = ('name', 'race', 'hero_class', 'hp', 'max_hp', 'strength', 'dex', 'defence', 'mana', 'owner_id', 'sala_id')
    if any(data.get(f) is None for f in required):
        return JsonResponse({'error': 'Faltan campos obligatorios'}, status=400)
    h = Hero.objects.create(
        name=data['name'],
        race=data['race'],
        hero_class=data['hero_class'],
        hp=data['hp'],
        max_hp=data['max_hp'],
        strength=data['strength'],
        dex=data['dex'],
        defence=data['defence'],
        mana=data['mana'],
        owner_id=data['owner_id'],
        sala_id=data['sala_id']
    )
    return JsonResponse({'id': h.id, 'name': h.name}, status=201)


@csrf_exempt
def heroes_sala(request, sala_id):
    if request.method != 'GET':
        return JsonResponse({'error': 'Método no permitido'}, status=405)
    qs = list(Hero.objects.filter(sala_id=sala_id).values(
        'id', 'name', 'race', 'hero_class', 'hp', 'max_hp',
        'strength', 'dex', 'defence', 'mana', 'level', 'xp', 'owner_id'
    ))
    return JsonResponse(qs, safe=False)


@csrf_exempt
def hero_detalle(request, hero_id):
    try:
        h = Hero.objects.get(id=hero_id)
    except Hero.DoesNotExist:
        return JsonResponse({'error': 'Héroe no encontrado'}, status=404)
    if request.method == 'GET':
        return JsonResponse({
            'id': h.id, 'name': h.name, 'hp': h.hp, 'max_hp': h.max_hp,
            'strength': h.strength, 'dex': h.dex, 'defence': h.defence, 'mana': h.mana,
            'level': h.level, 'xp': h.xp
        })
    if request.method == 'PUT':
        data = _body(request)
        old_level = h.level
        for field in ('hp', 'max_hp', 'strength', 'dex', 'defence', 'mana', 'level', 'xp'):
            if field in data:
                setattr(h, field, data[field])
        if 'level' in data and int(data['level']) != old_level:
            # Cambio manual de nivel por el máster:
            #  - Si SUBE de nivel, se aplica el crecimiento de stats
            #    (clase + raza) por cada nivel ganado.
            #  - Si BAJA de nivel, se resta ese mismo crecimiento por cada
            #    nivel perdido (con clamps: stats >= 0, max_hp >= 1).
            #  - El nivel nunca puede ser menor a 1.
            #  - La XP se reescala al mismo % de progreso del nuevo nivel.
            new_level = max(1, int(data['level']))
            if new_level > old_level:
                h.level = old_level
                for _ in range(new_level - old_level):
                    h.level += 1
                    apply_level_up(h)
            else:
                h.level = old_level
                for _ in range(old_level - new_level):
                    apply_level_down(h)
                    h.level -= 1
            old_needed = xp_to_next(old_level)
            pct = (h.xp / old_needed) if old_needed > 0 else 0
            new_needed = xp_to_next(h.level)
            h.xp = max(0, min(round(pct * new_needed), new_needed - 1))
        else:
            # Subida de nivel automática por XP: si la XP llega al umbral, sube
            # de nivel, arrastra el sobrante y aplica el crecimiento de stats
            # (clase + raza). Cada nivel exige más XP que el anterior.
            while h.xp >= xp_to_next(h.level):
                h.xp -= xp_to_next(h.level)
                h.level += 1
                apply_level_up(h)
        h.save()
        return JsonResponse({
            'id': h.id, 'hp': h.hp, 'max_hp': h.max_hp,
            'strength': h.strength, 'dex': h.dex, 'defence': h.defence, 'mana': h.mana,
            'level': h.level, 'xp': h.xp
        })
    if request.method == 'DELETE':
        h.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def items(request):
    if request.method == 'GET':
        sala_id = request.GET.get('sala_id')
        tipo = request.GET.get('tipo')
        qs = Item.objects.filter(sala_id=sala_id) if sala_id else Item.objects.all()
        if tipo:
            qs = qs.filter(item_type=tipo)
        return JsonResponse(list(qs.values(
            'id', 'name', 'item_type', 'consumable', 'description', 'bonus_stat', 'bonus_value'
        )), safe=False)
    if request.method == 'POST':
        data = _body(request)
        it = Item.objects.create(
            name=data['name'],
            item_type=data['item_type'],
            consumable=data.get('consumable', False),
            description=data.get('description', ''),
            bonus_stat=data.get('bonus_stat', 'none'),
            bonus_value=data.get('bonus_value', 0),
            sala_id=data.get('sala_id')
        )
        return JsonResponse({'id': it.id, 'name': it.name}, status=201)
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def item_detalle(request, item_id):
    try:
        it = Item.objects.get(id=item_id)
    except Item.DoesNotExist:
        return JsonResponse({'error': 'Item no encontrado'}, status=404)
    if request.method == 'DELETE':
        it.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def inventario(request, hero_id):
    if request.method == 'GET':
        entries = InventoryEntry.objects.filter(hero_id=hero_id).select_related('item')
        data = [
            {
                'item_id': e.item_id,
                'name': e.item.name,
                'item_type': e.item.item_type,
                'consumable': e.item.consumable,
                'description': e.item.description,
                'bonus_stat': e.item.bonus_stat,
                'bonus_value': e.item.bonus_value,
                'quantity': e.quantity,
                'equipped': e.equipped
            }
            for e in entries
        ]
        return JsonResponse(data, safe=False)
    if request.method == 'POST':
        data = _body(request)
        entry, created = InventoryEntry.objects.get_or_create(
            hero_id=hero_id,
            item_id=data['item_id'],
            defaults={'quantity': data.get('quantity', 1)}
        )
        if not created:
            entry.quantity += data.get('quantity', 1)
            entry.save()
        return JsonResponse({'hero_id': hero_id, 'item_id': entry.item_id, 'quantity': entry.quantity}, status=201)
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def inventario_item(request, hero_id, item_id):
    try:
        entry = InventoryEntry.objects.get(hero_id=hero_id, item_id=item_id)
    except InventoryEntry.DoesNotExist:
        return JsonResponse({'error': 'Entrada no encontrada'}, status=404)
    if request.method == 'PUT':
        data = _body(request)
        if 'equipped' in data:
            new_equipped = bool(data['equipped'])
            # Si el flag cambia, aplicamos o quitamos el bonus del item en
            # los stats del héroe. Simétrico, así equipar+desequipar se
            # cancela exacto.
            if new_equipped != entry.equipped:
                _apply_item_bonus(entry.hero, entry.item, sign=(1 if new_equipped else -1))
                entry.hero.save()
            entry.equipped = new_equipped
        if 'quantity' in data:
            entry.quantity = data['quantity']
        entry.save()
        return JsonResponse({'item_id': item_id, 'equipped': entry.equipped, 'quantity': entry.quantity})
    if request.method == 'DELETE':
        # Si se borra un item equipado, primero quitamos su bonus para que
        # no quede un efecto fantasma en el héroe.
        if entry.equipped:
            _apply_item_bonus(entry.hero, entry.item, sign=-1)
            entry.hero.save()
        entry.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def mapas_sala(request, sala_id):
    if request.method == 'GET':
        qs = list(GameMap.objects.filter(sala_id=sala_id).values('id', 'name', 'visible', 'image'))
        return JsonResponse(qs, safe=False)
    if request.method == 'POST':
        data = _body(request)
        m = GameMap.objects.create(
            name=data['name'],
            sala_id=sala_id,
            visible=data.get('visible', False),
            image=data.get('image', '')
        )
        return JsonResponse({'id': m.id, 'name': m.name, 'visible': m.visible, 'image': m.image}, status=201)
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def mapa_detalle(request, mapa_id):
    try:
        m = GameMap.objects.get(id=mapa_id)
    except GameMap.DoesNotExist:
        return JsonResponse({'error': 'Mapa no encontrado'}, status=404)
    if request.method == 'GET':
        return JsonResponse({'id': m.id, 'name': m.name, 'visible': m.visible, 'image': m.image})
    if request.method == 'PUT':
        data = _body(request)
        if 'visible' in data:
            m.visible = data['visible']
        if 'name' in data:
            m.name = data['name']
        if 'image' in data:
            m.image = data['image']
        m.save()
        return JsonResponse({'id': m.id, 'name': m.name, 'visible': m.visible, 'image': m.image})
    if request.method == 'DELETE':
        m.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)
