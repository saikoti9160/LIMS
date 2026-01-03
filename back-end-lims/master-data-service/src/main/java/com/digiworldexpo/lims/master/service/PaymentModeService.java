package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.PaymentMode;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface PaymentModeService {

	ResponseModel<PaymentMode> addPaymentMode(PaymentMode paymentMode, UUID createdBy);

	ResponseModel<List<PaymentMode>> getAllPaymentModes(String startsWith, int pageNumber, int pageSize,
			String sortedBy);

	ResponseModel<PaymentMode> updatePaymentModeById(UUID id, PaymentMode paymentMode, UUID modifiedBy);

	ResponseModel<PaymentMode> getPaymentModeById(UUID id);

	ResponseModel<PaymentMode> deletePaymentModeById(UUID id);
}
