package com.tasktracker.backend.dto;

import java.util.Map;

public record EmailSendingRequest(String to, String type, Map<String, String> params) {
}
