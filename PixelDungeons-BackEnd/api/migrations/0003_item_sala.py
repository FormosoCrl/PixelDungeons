from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0002_gamemap_image'),
    ]

    operations = [
        migrations.AddField(
            model_name='item',
            name='sala',
            field=models.ForeignKey(
                blank=True,
                null=True,
                on_delete=django.db.models.deletion.CASCADE,
                related_name='items',
                to='api.sala',
            ),
        ),
    ]
