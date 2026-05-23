import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.hashers import make_password, check_password
from .models import Usuario, Sala, Hero, Item, GameMap, InventoryEntry


def _body(request):
    return json.loads(request.body)


@csrf_exempt
def registro(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Método no permitido'}, status=405)
    data = _body(request)
    if Usuario.objects.filter(username=data['username']).exists():
        return JsonResponse({'error': 'Usuario ya existe'}, status=400)
    u = Usuario.objects.create(
        username=data['username'],
        password=make_password(data['password'])
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
        'strength', 'dex', 'defence', 'mana', 'owner_id'
    ))
    return JsonResponse(qs, safe=False)


@csrf_exempt
def hero_detalle(request, hero_id):
    try:
        h = Hero.objects.get(id=hero_id)
    except Hero.DoesNotExist:
        return JsonResponse({'error': 'Héroe no encontrado'}, status=404)
    if request.method == 'PUT':
        data = _body(request)
        for field in ('hp', 'max_hp', 'strength', 'dex', 'defence', 'mana'):
            if field in data:
                setattr(h, field, data[field])
        h.save()
        return JsonResponse({'id': h.id, 'hp': h.hp, 'max_hp': h.max_hp})
    if request.method == 'DELETE':
        h.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def items(request):
    if request.method == 'GET':
        tipo = request.GET.get('tipo')
        qs = Item.objects.all()
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
            bonus_value=data.get('bonus_value', 0)
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
            entry.equipped = data['equipped']
        if 'quantity' in data:
            entry.quantity = data['quantity']
        entry.save()
        return JsonResponse({'item_id': item_id, 'equipped': entry.equipped, 'quantity': entry.quantity})
    if request.method == 'DELETE':
        entry.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def mapas_sala(request, sala_id):
    if request.method == 'GET':
        qs = list(GameMap.objects.filter(sala_id=sala_id).values('id', 'name', 'visible'))
        return JsonResponse(qs, safe=False)
    if request.method == 'POST':
        data = _body(request)
        m = GameMap.objects.create(
            name=data['name'],
            sala_id=sala_id,
            visible=data.get('visible', False)
        )
        return JsonResponse({'id': m.id, 'name': m.name, 'visible': m.visible}, status=201)
    return JsonResponse({'error': 'Método no permitido'}, status=405)


@csrf_exempt
def mapa_detalle(request, mapa_id):
    try:
        m = GameMap.objects.get(id=mapa_id)
    except GameMap.DoesNotExist:
        return JsonResponse({'error': 'Mapa no encontrado'}, status=404)
    if request.method == 'PUT':
        data = _body(request)
        if 'visible' in data:
            m.visible = data['visible']
        if 'name' in data:
            m.name = data['name']
        m.save()
        return JsonResponse({'id': m.id, 'name': m.name, 'visible': m.visible})
    if request.method == 'DELETE':
        m.delete()
        return JsonResponse({'ok': True})
    return JsonResponse({'error': 'Método no permitido'}, status=405)
