from django.urls import path
from .views import TestUserView, create_event, EventListView, EventDetailView

urlpatterns = [
    path('test-user/', TestUserView.as_view(), name='test-user'),
    path('events/create/', create_event, name='create-event'),
    path('events/', EventListView.as_view(), name='list-events'),
    path('events/<int:pk>/', EventDetailView.as_view(), name='detail-event'),
]
