package org.wangyl.jiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.jiji.common.BaseContext;
import org.wangyl.jiji.common.CustomException;
import org.wangyl.jiji.common.ShiroUtils;
import org.wangyl.jiji.dao.OrdersMapper;
import org.wangyl.jiji.entity.*;
import org.wangyl.jiji.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    //用户下单
    @Override
    @Transactional
    public void submit(Orders orders) {
        //通过basecontext获取用户ID
        //Long currentId = BaseContext.getCurrentId();
        Long currentId = ShiroUtils.getEmployeeOrUserId();
        // 通过用户ID查询购物车
        // select * from shopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空，不能下单");
        }
        // 再查用户表和地址表
        User user = userService.getById(currentId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("地址信息有误，不能下单");
        }
        // 向订单插入1条数据
        long orderId = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger(0);//原子操作，避免多线程高并发下数据不一致
        //除了计算amount，还封装好orderDetails
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);//在派送
        orders.setAmount(new BigDecimal(amount.get()));//后端计算得到的总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);
        // 向订单明细表插入数据（可能是多条）
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
