from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0003_item_sala'),
    ]

    operations = [
        migrations.AddField(
            model_name='hero',
            name='level',
            field=models.IntegerField(default=1),
        ),
        migrations.AddField(
            model_name='hero',
            name='xp',
            field=models.IntegerField(default=0),
        ),
    ]
