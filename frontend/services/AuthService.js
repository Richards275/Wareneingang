const API_URL = 'http://localhost:8080/api/v1/'

class AuthService {
  getHeaderMitToken(token) {
    return token ? { headers: { Authorization: 'Bearer ' + token } } : null
  }

  getHeaderMitBearerTokenAndMultipartFile(onUploadProgress, token) {
    const header = {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + token,
      },
      onUploadProgress,
    }
    return token ? header : null
  }
}

const authservice = new AuthService()

export { authservice, API_URL }
