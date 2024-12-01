from django.urls import path
from .views import (
    TestUserView, 
    create_event, 
    EventListView, 
    EventDetailView, 
    rsvp_event,
    comment_event,
    UserEventHistoryView,
    TodayAndFutureEventsView
) # Importa todas las vistas del módulo views

urlpatterns = [
    path('test-user/', TestUserView.as_view(), name='test-user'),
    path('events/create/', create_event, name='create-event'),
    path('events/', EventListView.as_view(), name='list-events'),
    path('events/<int:pk>/', EventDetailView.as_view(), name='detail-event'),
    path('events/<int:event_id>/rsvp/', rsvp_event, name='rsvp-event'),
    path('events/<int:event_id>/comment/', comment_event, name='comment-event'),
    path('users/<int:user_id>/history/', UserEventHistoryView.as_view(), name='user-event-history'),
    path('today-and-future-events/', TodayAndFutureEventsView.as_view(), name='today-and-future-events'),
]
