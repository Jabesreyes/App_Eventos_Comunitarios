from rest_framework import serializers
from .models import Event

class EventSerializer(serializers.ModelSerializer):
    class Meta:
        model = Event
        fields = ['id', 'organizer', 'title', 'description', 'date', 'time', 'location', 'created_at', 'updated_at']
        read_only_fields = ['organizer', 'created_at', 'updated_at']
