from rest_framework import serializers
from .models import Event, RSVP, Comment

class EventSerializer(serializers.ModelSerializer):
    class Meta:
        model = Event
        fields = ['id', 'organizer', 'title', 'description', 'date', 'time', 'location', 'created_at', 'updated_at']
        read_only_fields = ['organizer', 'created_at', 'updated_at']

class RSVPSerializer(serializers.ModelSerializer):
    class Meta:
        model = RSVP
        fields = ['id', 'user', 'event', 'status', 'created_at']
        read_only_fields = ['user', 'created_at'] 

class CommentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Comment
        fields = ['id', 'event', 'user', 'text', 'created_at']
        read_only_fields = ['user', 'created_at']

class UserEventHistorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Event
        fields = ['id', 'title', 'description', 'date', 'time', 'location', 'created_at', 'updated_at']
        read_only_fields = ['id', 'title', 'description', 'date', 'time', 'location', 'created_at', 'updated_at']