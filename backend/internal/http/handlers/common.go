package handlers

import (
	"net/http"

	httpapi "summer-school-2026/backend/internal/http"
)

const (
	msgBadRequest   = "Неверные параметры запроса. Проверьте корректность переданных значений."
	msgUnauthorized = "Требуется авторизация. Передайте действительный токен в заголовке Authorization."
	msgInternal     = "Что-то пошло не так. Попробуйте ещё раз позже."
)

func bearerOrUnauthorized(w http.ResponseWriter, r *http.Request) (string, bool) {
	token, err := httpapi.BearerToken(r)
	if err != nil {
		httpapi.WriteError(w, http.StatusUnauthorized, httpapi.CodeUnauthorized, msgUnauthorized, nil)
		return "", false
	}
	return token, true
}

func decodeOrBadRequest(w http.ResponseWriter, r *http.Request, dst any) bool {
	if err := httpapi.DecodeJSON(r, dst); err != nil {
		httpapi.WriteError(w, http.StatusBadRequest, httpapi.CodeBadRequest, msgBadRequest, nil)
		return false
	}
	return true
}

func writeInternalError(w http.ResponseWriter) {
	httpapi.WriteError(w, http.StatusInternalServerError, httpapi.CodeInternalError, msgInternal, nil)
}

func pagination(w http.ResponseWriter, limitParam, offsetParam *int) (int, int, bool) {
	limit := 20
	if limitParam != nil {
		limit = *limitParam
	}
	offset := 0
	if offsetParam != nil {
		offset = *offsetParam
	}
	if limit < 1 || limit > 100 || offset < 0 {
		httpapi.WriteError(w, http.StatusBadRequest, httpapi.CodeBadRequest, msgBadRequest, nil)
		return 0, 0, false
	}
	return limit, offset, true
}