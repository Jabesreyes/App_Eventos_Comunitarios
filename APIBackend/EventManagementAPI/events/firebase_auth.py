from firebase_admin import auth
from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from django.contrib.auth.models import User


class FirebaseAuthentication(BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')

        if not auth_header or not auth_header.startswith('Bearer '):
            return None  # No se proporcionó token

        id_token = auth_header.split(' ')[1]

        try:
            # Verificar el token con Firebase
            decoded_token = auth.verify_id_token(id_token)
            uid = decoded_token['uid']

            # Obtener el usuario asociado al token
            user, created = User.objects.get_or_create(username=uid)


            return (user, None)
        except Exception as e:
            raise AuthenticationFailed(f"Error al autenticar: {str(e)}")
