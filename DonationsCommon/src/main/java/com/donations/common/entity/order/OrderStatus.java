package com.donations.common.entity.order;

public enum OrderStatus {

	NEW {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã được đặt bởi khách hàng";
		}

	},

	CANCELLED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã bị từ chối";
		}
	},

	PROCESSING {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đang được xử lý";
		}
	},

	PACKAGED {
		@Override
		public String defaultDescription() {
			return "Sản phẩm đã được đóng gói";
		}
	},

	PICKED {
		@Override
		public String defaultDescription() {
			return "Người giao hàng đã lấy hàng";
		}
	},

	SHIPPING {
		@Override
		public String defaultDescription() {
			return "Người giao hàng đang giao hàng";
		}
	},

	DELIVERED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã nhận sản phẩm";
		}
	},

	RETURN_REQUESTED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã gửi yêu cầu trả hàng";
		}
	},

	RETURNED {
		@Override
		public String defaultDescription() {
			return "Sản phẩm đã được trả về";
		}
	},

	PAID {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã thanh toán đơn hàng này";
		}
	},

	REFUNDED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã được hoàn tiền";
		}
	};

	public abstract String defaultDescription();
}
