package org.wangyl.reggie.dto;

import org.wangyl.reggie.entity.OrderDetail;
import org.wangyl.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
