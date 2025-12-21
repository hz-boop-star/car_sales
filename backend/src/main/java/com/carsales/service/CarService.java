package com.carsales.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.dto.CarCreateRequest;
import com.carsales.dto.CarImportDTO;
import com.carsales.dto.CarQueryRequest;
import com.carsales.dto.CarUpdateRequest;
import com.carsales.entity.CarInfo;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.CarMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆服务类
 * 处理车辆管理相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;

    /**
     * 创建车辆
     * 
     * @param request 车辆创建请求
     * @return 创建的车辆信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CarInfo createCar(CarCreateRequest request) {
        // 检查VIN是否已存在
        LambdaQueryWrapper<CarInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarInfo::getVin, request.getVin());
        Long count = carMapper.selectCount(queryWrapper);

        if (count > 0) {
            log.warn("车辆创建失败：VIN已存在 - {}", request.getVin());
            throw new BusinessException(3004, "车架号已存在");
        }

        // 创建车辆实体
        CarInfo carInfo = new CarInfo();
        BeanUtils.copyProperties(request, carInfo);
        carInfo.setStatus(0); // 初始状态为"在库"

        // 保存到数据库
        carMapper.insert(carInfo);

        log.info("车辆创建成功 - VIN: {}, 品牌: {}, 型号: {}", carInfo.getVin(), carInfo.getBrand(), carInfo.getModel());

        return carInfo;
    }

    /**
     * 更新车辆信息
     * 
     * @param request 车辆更新请求
     * @return 更新后的车辆信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CarInfo updateCar(CarUpdateRequest request) {
        // 检查车辆是否存在
        CarInfo existingCar = carMapper.selectById(request.getId());
        if (existingCar == null) {
            log.warn("车辆更新失败：车辆不存在 - ID: {}", request.getId());
            throw new BusinessException(3001, "车辆不存在");
        }

        // 如果更新VIN，检查新VIN是否已被其他车辆使用
        if (request.getVin() != null && !request.getVin().equals(existingCar.getVin())) {
            LambdaQueryWrapper<CarInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CarInfo::getVin, request.getVin())
                    .ne(CarInfo::getId, request.getId());
            Long count = carMapper.selectCount(queryWrapper);

            if (count > 0) {
                log.warn("车辆更新失败：VIN已被其他车辆使用 - {}", request.getVin());
                throw new BusinessException(3004, "车架号已存在");
            }
        }

        // 更新车辆信息（只更新非null字段）
        CarInfo carInfo = new CarInfo();
        BeanUtils.copyProperties(request, carInfo);
        carMapper.updateById(carInfo);

        log.info("车辆更新成功 - ID: {}, VIN: {}", request.getId(), request.getVin());

        // 返回更新后的完整信息
        return carMapper.selectById(request.getId());
    }

    /**
     * 删除车辆
     * 
     * @param id 车辆ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCar(Long id) {
        // 检查车辆是否存在
        CarInfo carInfo = carMapper.selectById(id);
        if (carInfo == null) {
            log.warn("车辆删除失败：车辆不存在 - ID: {}", id);
            throw new BusinessException(3001, "车辆不存在");
        }

        // 检查车辆状态，已售车辆不能删除
        if (carInfo.getStatus() == 2) {
            log.warn("车辆删除失败：已售车辆不能删除 - ID: {}, VIN: {}", id, carInfo.getVin());
            throw new BusinessException(3006, "已售车辆不能删除");
        }

        // 删除车辆
        carMapper.deleteById(id);

        log.info("车辆删除成功 - ID: {}, VIN: {}", id, carInfo.getVin());
    }

    /**
     * 根据ID查询车辆
     * 
     * @param id 车辆ID
     * @return 车辆信息
     */
    public CarInfo getCarById(Long id) {
        CarInfo carInfo = carMapper.selectById(id);
        if (carInfo == null) {
            throw new BusinessException(3001, "车辆不存在");
        }
        return carInfo;
    }

    /**
     * 分页查询车辆列表（支持多条件筛选）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    public Page<CarInfo> queryCarList(CarQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<CarInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 品牌精确匹配
        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            queryWrapper.eq(CarInfo::getBrand, request.getBrand());
        }

        // 价格区间查询
        if (request.getMinPrice() != null) {
            queryWrapper.ge(CarInfo::getPrice, request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            queryWrapper.le(CarInfo::getPrice, request.getMaxPrice());
        }

        // 状态筛选
        if (request.getStatus() != null) {
            queryWrapper.eq(CarInfo::getStatus, request.getStatus());
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(CarInfo::getCreateTime);

        // 分页查询
        Page<CarInfo> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<CarInfo> result = carMapper.selectPage(page, queryWrapper);

        log.debug("车辆查询 - 品牌: {}, 价格区间: [{}, {}], 状态: {}, 结果数: {}",
                request.getBrand(), request.getMinPrice(), request.getMaxPrice(),
                request.getStatus(), result.getRecords().size());

        return result;
    }

    /**
     * 批量导入车辆
     * 
     * @param file Excel 文件
     * @return 导入结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importCars(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try {
            // 读取 Excel 文件
            List<CarImportDTO> importList = EasyExcel.read(file.getInputStream())
                    .head(CarImportDTO.class)
                    .sheet()
                    .doReadSync();

            log.info("开始导入车辆 - 总数: {}", importList.size());

            // 验证数据
            List<CarInfo> validCars = new ArrayList<>();
            for (int i = 0; i < importList.size(); i++) {
                CarImportDTO dto = importList.get(i);
                int rowNum = i + 2; // Excel 行号（从2开始，因为第1行是表头）

                try {
                    // 验证必填字段
                    if (dto.getVin() == null || dto.getVin().trim().isEmpty()) {
                        errors.add("第" + rowNum + "行：车架号不能为空");
                        failCount++;
                        continue;
                    }
                    if (dto.getBrand() == null || dto.getBrand().trim().isEmpty()) {
                        errors.add("第" + rowNum + "行：品牌不能为空");
                        failCount++;
                        continue;
                    }
                    if (dto.getModel() == null || dto.getModel().trim().isEmpty()) {
                        errors.add("第" + rowNum + "行：型号不能为空");
                        failCount++;
                        continue;
                    }
                    if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        errors.add("第" + rowNum + "行：价格必须大于0");
                        failCount++;
                        continue;
                    }

                    // 检查 VIN 是否已存在
                    LambdaQueryWrapper<CarInfo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(CarInfo::getVin, dto.getVin());
                    Long count = carMapper.selectCount(queryWrapper);
                    if (count > 0) {
                        errors.add("第" + rowNum + "行：车架号 " + dto.getVin() + " 已存在");
                        failCount++;
                        continue;
                    }

                    // 转换为实体
                    CarInfo carInfo = new CarInfo();
                    BeanUtils.copyProperties(dto, carInfo);
                    // 如果 Excel 没有填状态，默认为"在库"
                    if (carInfo.getStatus() == null) {
                        carInfo.setStatus(0);
                    }
                    validCars.add(carInfo);

                } catch (Exception e) {
                    errors.add("第" + rowNum + "行：数据格式错误 - " + e.getMessage());
                    failCount++;
                }
            }

            // 如果有任何验证失败，回滚整个事务
            if (!errors.isEmpty()) {
                log.warn("车辆导入验证失败 - 失败数: {}", failCount);
                String errorMessage = "数据验证失败：" + String.join("; ", errors);
                throw new BusinessException(5002, errorMessage);
            }

            // 批量插入
            for (CarInfo carInfo : validCars) {
                carMapper.insert(carInfo);
                successCount++;
            }

            log.info("车辆导入成功 - 成功数: {}", successCount);

        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException(5001, "文件上传失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("车辆导入失败", e);
            throw new BusinessException(5004, "导入失败：" + e.getMessage());
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errors", errors);

        return result;
    }

    /**
     * 导出车辆列表
     * 
     * @param request 查询条件
     * @return 车辆列表
     */
    public List<CarInfo> exportCars(CarQueryRequest request) {
        // 构建查询条件（与 queryCarList 相同，但不分页）
        LambdaQueryWrapper<CarInfo> queryWrapper = new LambdaQueryWrapper<>();

        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            queryWrapper.eq(CarInfo::getBrand, request.getBrand());
        }
        if (request.getMinPrice() != null) {
            queryWrapper.ge(CarInfo::getPrice, request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            queryWrapper.le(CarInfo::getPrice, request.getMaxPrice());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(CarInfo::getStatus, request.getStatus());
        }

        queryWrapper.orderByDesc(CarInfo::getCreateTime);

        List<CarInfo> carList = carMapper.selectList(queryWrapper);

        log.info("导出车辆列表 - 数量: {}", carList.size());

        return carList;
    }
}
