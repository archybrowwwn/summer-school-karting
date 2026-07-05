package handlers

import (
	"errors"
	"net/http"

	httpapi "summer-school-2026/backend/internal/http"
	authapi "summer-school-2026/backend/internal/http/openapi/auth"
	"summer-school-2026/backend/internal/service/auth"

	"github.com/google/uuid"
)

type AuthHandler struct {
	authapi.Unimplemented
	service *auth.Service
}

func NewAuthHandler(service *auth.Service) *AuthHandler {
	return &AuthHandler{service: service}
}

func (h *AuthHandler) Logout(w http.ResponseWriter, r *http.Request) {
	token, ok := bearerOrUnauthorized(w, r)
	if !ok {
		return
	}
	if err := h.service.Logout(r.Context(), token); err != nil {
		httpapi.WriteError(w, http.StatusUnauthorized, httpapi.CodeUnauthorized, msgUnauthorized, nil)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (h *AuthHandler) RequestAuthCode(w http.ResponseWriter, r *http.Request) {
	var req authapi.RequestCodeRequest
	if !decodeOrBadRequest(w, r, &req) {
		return
	}

	result, err := h.service.RequestCode(r.Context(), req.Phone)
	if err != nil {
		writeAuthError(w, err)
		return
	}

	httpapi.WriteJSON(w, http.StatusOK, authapi.RequestCodeResponse{
		TtlSeconds:         result.TTLSeconds,
		ResendAfterSeconds: result.ResendAfterSeconds,
		Code:               &result.Code,
	})
}

func (h *AuthHandler) VerifyAuthCode(w http.ResponseWriter, r *http.Request) {
	var req authapi.VerifyCodeRequest
	if !decodeOrBadRequest(w, r, &req) {
		return
	}

	result, err := h.service.VerifyCode(r.Context(), req.Phone, req.Code)
	if err != nil {
		writeAuthError(w, err)
		return
	}

	clientID, err := uuid.Parse(result.Client.ID)
	if err != nil {
		writeInternalError(w)
		return
	}

	httpapi.WriteJSON(w, http.StatusOK, authapi.VerifyCodeResponse{
		Tokens: authapi.TokenPair{
			AccessToken:  result.Token,
			RefreshToken: result.Token,
			ExpiresIn:    24 * 60 * 60,
			TokenType:    authapi.Bearer,
		},
		Client: authapi.Client{
			Id:        clientID,
			Name:      result.Client.Name,
			Phone:     result.Client.Phone,
			CreatedAt: result.Client.CreatedAt,
		},
		IsNew: result.IsNew,
	})
}

func writeAuthError(w http.ResponseWriter, err error) {
	switch {
	case errors.Is(err, auth.ErrInvalidPhone):
		httpapi.WriteError(w, http.StatusBadRequest, httpapi.CodeBadRequest, msgBadRequest, nil)
	case errors.Is(err, auth.ErrInvalidCode):
		httpapi.WriteError(w, http.StatusBadRequest, httpapi.CodeInvalidCode, "Неверный или истёкший код подтверждения.", nil)
	case errors.Is(err, auth.ErrTooManyRequests):
		httpapi.WriteError(w, http.StatusTooManyRequests, httpapi.CodeTooManyRequests, "Слишком много запросов. Повторите попытку позже.", nil)
	default:
		writeInternalError(w)
	}
}