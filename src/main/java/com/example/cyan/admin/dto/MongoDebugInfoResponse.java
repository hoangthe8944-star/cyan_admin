package com.example.cyan.admin.dto;

import java.util.List;

public record MongoDebugInfoResponse(
        String database,
        List<String> hosts,
        String scheme,
        String configuredUriMasked) {
}
