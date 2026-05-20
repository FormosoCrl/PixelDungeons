from django.urls import path
from . import views

urlpatterns = [
    path('registro/', views.registro),
    path('login/', views.login),

    path('salas/', views.salas),
    path('salas/<int:sala_id>/', views.sala_detalle),
    path('salas/<int:sala_id>/heroes/', views.heroes_sala),
    path('salas/<int:sala_id>/mapas/', views.mapas_sala),

    path('heroes/', views.heroes),
    path('heroes/<int:hero_id>/', views.hero_detalle),
    path('heroes/<int:hero_id>/inventario/', views.inventario),
    path('heroes/<int:hero_id>/inventario/<int:item_id>/', views.inventario_item),

    path('items/', views.items),
    path('items/<int:item_id>/', views.item_detalle),

    path('mapas/<int:mapa_id>/', views.mapa_detalle),
]
