package com.pls.ltlrating.integration.ltllifecycle.dto.request;

import lombok.Data;

@Data
public class ShipmentCancelRequestDTO {

  /** The application requesting the cancellation. */
  private String requesterApp;

  private String loadUUID;

}
