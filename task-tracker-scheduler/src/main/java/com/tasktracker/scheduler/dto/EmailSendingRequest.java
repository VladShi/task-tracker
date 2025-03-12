package com.tasktracker.scheduler.dto;

import java.util.Map;

public record EmailSendingRequest(String to, String type, Map<String, String> params) {
}
