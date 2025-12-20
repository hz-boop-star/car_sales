package com.carsales.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.annotation.OperationLog;
import com.carsales.annotation.RequireRole;
import com.carsales.common.Result;
import com.carsales.dto.CarCreateRequest;
import com.carsales.dto.CarQueryRequest;
import com.carsales.dto.CarUpdateRequest;
import com.carsales.entity.CarInfo;
import com.carsales.service.CarService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 车辆管理控制器
 * 提供车辆CRUD和查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    /**
     * 创建车辆
     * 
     * @param request 车辆创建请求
     * @return 创建的车辆信息
     */
    @PostMapping
    @RequireRole({ "ADMIN" })
    @OperationLog("创建车辆")
    public Result<CarInfo> createCar(@Valid @RequestBody CarCreateRequest request) {
        log.info("收到车辆创建请求 - VIN: {}, 品牌: {}, 型号: {}",
                request.getVin(), request.getBrand(), request.getModel());
        CarInfo carInfo = carService.createCar(request);
        return Result.success("车辆创建成功", carInfo);
    }

    /**
     * 更新车辆信息
     * 
     * @param id      车辆ID
     * @param request 车辆更新请求
     * @return 更新后的车辆信息
     */
    @PutMapping("/{id}")
    @RequireRole({ "ADMIN" })
    public Result<CarInfo> updateCar(@PathVariable Long id,
            @Valid @RequestBody CarUpdateRequest request) {
        log.info("收到车辆更新请求 - ID: {}", id);
        request.setId(id);
        CarInfo carInfo = carService.updateCar(request);
        return Result.success("车辆更新成功", carInfo);
    }

    /**
     * 删除车辆
     * 
     * @param id 车辆ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    @RequireRole({ "ADMIN" })
    @OperationLog("删除车辆")
    public Result<String> deleteCar(@PathVariable Long id) {
        log.info("收到车辆删除请求 - ID: {}", id);
        carService.deleteCar(id);
        return Result.success("车辆删除成功");
    }

    /**
     * 根据ID查询车辆
     * 
     * @param id 车辆ID
     * @return 车辆信息
     */
    @GetMapping("/{id}")
    public Result<CarInfo> getCarById(@PathVariable Long id) {
        log.info("收到车辆查询请求 - ID: {}", id);
        CarInfo carInfo = carService.getCarById(id);
        return Result.success(carInfo);
    }

    /**
     * 分页查询车辆列表（支持多条件筛选）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping
    public Result<Page<CarInfo>> queryCarList(CarQueryRequest request) {
        log.info("收到车辆列表查询请求 - 品牌: {}, 价格区间: [{}, {}], 状态: {}, 页码: {}",
                request.getBrand(), request.getMinPrice(), request.getMaxPrice(),
                request.getStatus(), request.getPageNum());
        Page<CarInfo> page = carService.queryCarList(request);
        return Result.success(page);
    }

    /**
     * 批量导入车辆
     * 
     * @param file Excel 文件
     * @return 导入结果
     */
    @PostMapping("/import")
    @RequireRole({ "ADMIN" })
    @OperationLog("批量导入车辆")
    public Result<Map<String, Object>> importCars(@RequestParam("file") MultipartFile file) {
        log.info("收到车辆导入请求 - 文件名: {}, 大小: {} bytes",
                file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            return Result.error(5001, "文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error(5002, "文件格式错误，只支持 .xlsx 或 .xls 格式");
        }

        Map<String, Object> result = carService.importCars(file);
        return Result.success("导入完成", result);
    }

    /**
     * 导出车辆列表
     * 
     * @param request  查询条件
     * @param response HTTP 响应
     */
    @GetMapping("/export")
    public void exportCars(CarQueryRequest request, HttpServletResponse response) {
        log.info("收到车辆导出请求 - 品牌: {}, 价格区间: [{}, {}], 状态: {}",
                request.getBrand(), request.getMinPrice(), request.getMaxPrice(), request.getStatus());

        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            String fileName = URLEncoder.encode("车辆列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 查询数据
            List<CarInfo> carList = carService.exportCars(request);

            // 写入 Excel
            EasyExcel.write(response.getOutputStream(), CarInfo.class)
                    .sheet("车辆列表")
                    .doWrite(carList);

            log.info("车辆导出成功 - 数量: {}", carList.size());

        } catch (IOException e) {
            log.error("车辆导出失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
