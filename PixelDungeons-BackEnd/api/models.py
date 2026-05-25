from django.db import models


class Usuario(models.Model):
    username = models.CharField(max_length=50, unique=True)
    password = models.CharField(max_length=128)


class Sala(models.Model):
    codigo = models.CharField(max_length=20, unique=True)
    nombre = models.CharField(max_length=100)
    master = models.ForeignKey(Usuario, on_delete=models.CASCADE, related_name='salas_creadas')


class Hero(models.Model):
    name = models.CharField(max_length=50)
    race = models.CharField(max_length=30)
    hero_class = models.CharField(max_length=30)
    hp = models.IntegerField()
    max_hp = models.IntegerField()
    strength = models.IntegerField()
    dex = models.IntegerField()
    defence = models.IntegerField()
    mana = models.IntegerField()
    owner = models.ForeignKey(Usuario, on_delete=models.CASCADE, related_name='heroes')
    sala = models.ForeignKey(Sala, on_delete=models.CASCADE, related_name='heroes')


class Item(models.Model):
    name = models.CharField(max_length=50)
    item_type = models.CharField(max_length=30)
    consumable = models.BooleanField(default=False)
    description = models.CharField(max_length=200)
    bonus_stat = models.CharField(max_length=10, default='none')
    bonus_value = models.IntegerField(default=0)
    sala = models.ForeignKey(Sala, on_delete=models.CASCADE, related_name='items', null=True, blank=True)


class GameMap(models.Model):
    name = models.CharField(max_length=100)
    sala = models.ForeignKey(Sala, on_delete=models.CASCADE, related_name='maps')
    visible = models.BooleanField(default=False)
    image = models.TextField(blank=True, default='')


class InventoryEntry(models.Model):
    hero = models.ForeignKey(Hero, on_delete=models.CASCADE, related_name='inventory')
    item = models.ForeignKey(Item, on_delete=models.CASCADE)
    quantity = models.IntegerField(default=1)
    equipped = models.BooleanField(default=False)
