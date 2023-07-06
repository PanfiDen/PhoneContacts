package com.chiacademy.phonecontacts.contact.model.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponse {
    Long id;
    String status;
}
