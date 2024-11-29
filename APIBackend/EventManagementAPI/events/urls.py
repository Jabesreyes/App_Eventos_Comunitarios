from django.urls import path
from .views import TestUserView

urlpatterns = [
    path('test-user/', TestUserView.as_view(), name='test-user'),
]
