package com.civicdesk.module.iam.service;

import com.civicdesk.common.response.PageResponse;
import com.civicdesk.module.iam.dto.response.AuditLogResponse;

public interface AuditService {

    
    void log(String userId, String action, String module, String ip);

    PageResponse<AuditLogResponse> getAll(String userId, String action, String module, int page, int size);

    AuditLogResponse getById(String auditId);
}
