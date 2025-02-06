package com.connect.codeness.global.enums;

public enum FieldType {
	BACKEND("백엔드"),
	FRONTEND("프론트엔드"),
	GAME("게임"),
	AI("인공지능"),
	SERVER_INFRA("서버_인프라"),
	NETWORK_SECURITY("네트워크_보안"),
	EMBEDDED_SYSTEMS("임베디드");

	private final String fieldTypeText;

	FieldType(String fieldTypeText) {
		this.fieldTypeText = fieldTypeText;
	}
}
