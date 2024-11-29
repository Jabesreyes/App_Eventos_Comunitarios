from rest_framework import status
from datetime import datetime, timedelta
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import generics
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes
from .models import Event
from .serializers import EventSerializer

class TestUserView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        user_uid = request.user  # Obtiene el UID del usuario autenticado
        return Response({"message": "Usuario autenticado", "user_uid": user_uid})

    def post(self, request):
        return Response({"message": "POST método también permitido"})

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def create_event(request):
    if request.method == 'POST':
        # Obtén los datos del evento desde el cuerpo de la solicitud
        title = request.data.get('title')
        description = request.data.get('description')
        date = request.data.get('date')
        time = request.data.get('time')
        location = request.data.get('location')

        # Verificar si el título del evento está vacío
        if not title or not title.strip():
            return Response({"detail": "El título del evento no puede estar vacío."}, status=status.HTTP_400_BAD_REQUEST)

        # Combina la fecha y la hora en un objeto de datetime para compararlo fácilmente
        event_datetime = datetime.combine(datetime.strptime(date, "%Y-%m-%d").date(), datetime.strptime(time, "%H:%M:%S").time())

        # Verificar que la fecha y hora del evento no sean en el pasado
        if event_datetime < datetime.now():
            return Response({"detail": "La fecha y hora del evento no pueden ser en el pasado."}, status=status.HTTP_400_BAD_REQUEST)

        # Verificar si ya existe un evento en el mismo lugar y hora, y asegurarse de que la diferencia sea de al menos 1 hora
        conflicting_event = Event.objects.filter(location=location, date=date).exclude(id=request.data.get('id', None))
        
        for event in conflicting_event:
            existing_event_datetime = datetime.combine(event.date, event.time)
            time_difference = abs((event_datetime - existing_event_datetime).total_seconds())
            
            # Verificar que la diferencia sea mayor o igual a 1 hora (3600 segundos)
            if time_difference < 3600:  # 3600 segundos = 1 hora
                return Response({"detail": "Ya existe un evento en este lugar a la misma hora."}, status=status.HTTP_400_BAD_REQUEST)

        # Si pasa la validación, guarda el evento
        serializer = EventSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save(organizer=request.user)  # Asignar al organizador (usuario autenticado)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class EventListView(generics.ListAPIView):
    queryset = Event.objects.all().order_by('date', 'time')  # Ordenar por fecha y hora
    serializer_class = EventSerializer

class EventDetailView(generics.RetrieveAPIView):
    queryset = Event.objects.all()
    serializer_class = EventSerializer