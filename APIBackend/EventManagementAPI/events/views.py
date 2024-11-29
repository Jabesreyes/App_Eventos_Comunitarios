from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated

class TestUserView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        user_uid = request.user  # Obtiene el UID del usuario autenticado
        return Response({"message": "Usuario autenticado", "user_uid": user_uid})

    def post(self, request):
        return Response({"message": "POST método también permitido"})