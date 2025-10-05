/*
 * MIT License
 *
 * Copyright (c) 2025 com.gientech.agentops
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gientech.agentops.mcp.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.ai.mcp.server.name}")
    private String mcpServerName;

    @Value("${spring.ai.mcp.server.version}")
    private String mcpServerVersion;

    @Value("${spring.ai.mcp.server.protocol}")
    private String mcpServerProtocol;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/")
    public String status(Model model) {
        // 添加应用程序基本信息
        model.addAttribute("applicationName", applicationName);
        model.addAttribute("serverPort", serverPort);
        model.addAttribute("currentTime", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osVersion", System.getProperty("os.version"));

        // 添加MCP服务器信息
        model.addAttribute("mcpServerName", mcpServerName);
        model.addAttribute("mcpServerVersion", mcpServerVersion);
        model.addAttribute("mcpServerProtocol", mcpServerProtocol);
        
        // 使用反射机制扫描所有带有@McpTool注解的方法
        List<McpToolInfo> toolInfos = scanMcpTools();
        
        // 设置工具数量
        model.addAttribute("toolCount", toolInfos.size());
        
        // 按Provider类名对工具进行分组
        Map<String, List<McpToolInfo>> toolsByProvider = new HashMap<>();
        List<String> providerNames = new ArrayList<>();
        
        for (McpToolInfo toolInfo : toolInfos) {
            String className = toolInfo.getClassName();
            // 提取简单类名
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            
            // 按简单类名分组
            toolsByProvider.computeIfAbsent(simpleClassName, k -> new ArrayList<>()).add(toolInfo);
            
            // 记录provider名称（避免重复）
            if (!providerNames.contains(simpleClassName)) {
                providerNames.add(simpleClassName);
            }
        }
        
        // 将分组信息传递给前端
        model.addAttribute("toolsByProvider", toolsByProvider);
        model.addAttribute("providerNames", providerNames);
        
        return "status";
    }
    
    /**
     * 扫描所有带有@McpTool注解的方法
     * @return 工具信息列表
     */
    private List<McpToolInfo> scanMcpTools() {
        List<McpToolInfo> toolInfos = new ArrayList<>();
        
        try {
            // 获取所有Bean
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            
            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                Class<?> beanClass = bean.getClass();
                
                // 如果是代理类，获取原始类
                if (beanClass.getName().contains("$") && !beanClass.getName().contains("EnhancerBySpringCGLIB")) {
                    beanClass = beanClass.getSuperclass();
                }
                
                // 扫描所有方法
                Method[] methods = beanClass.getDeclaredMethods();
                for (Method method : methods) {
                    // 检查方法是否带有@McpTool注解
                    if (method.isAnnotationPresent(McpTool.class)) {
                        McpTool annotation = method.getAnnotation(McpTool.class);
                        
                        // 创建工具信息
                        McpToolInfo toolInfo = new McpToolInfo();
                        toolInfo.setName(method.getName());
                        toolInfo.setDescription(annotation.description());
                        toolInfo.setClassName(beanClass.getName());
                        toolInfo.setBeanName(beanName);
                        
                        // 收集参数信息
                        List<String> params = new ArrayList<>();
                        List<Map<String, String>> paramDetails = new ArrayList<>();
                        
                        Class<?>[] paramTypes = method.getParameterTypes();
                        Annotation[][] paramAnnotations = method.getParameterAnnotations();
                        
                        for (int i = 0; i < paramTypes.length; i++) {
                            Class<?> paramType = paramTypes[i];
                            params.add(paramType.getSimpleName());
                            
                            // 创建参数详细信息
                            Map<String, String> paramDetail = new HashMap<>();
                            paramDetail.put("type", paramType.getSimpleName());
                            
                            // 尝试获取参数名称（使用参数索引作为默认名称）
                            String paramName = "param" + i;
                            paramDetail.put("name", paramName);
                            
                            // 尝试获取参数描述
                            String paramDescription = "未提供描述";
                            for (Annotation paramAnnotation : paramAnnotations[i]) {
                                if (paramAnnotation instanceof McpToolParam) {
                                    McpToolParam mcpParamAnnotation = (McpToolParam) paramAnnotation;
                                    paramDescription = mcpParamAnnotation.description();
                                    break;
                                }
                            }
                            paramDetail.put("description", paramDescription);
                            
                            paramDetails.add(paramDetail);
                        }
                        
                        toolInfo.setParameters(params);
                        toolInfo.setParamDetails(paramDetails);
                        
                        toolInfos.add(toolInfo);
                    }
                }
            }
            
            logger.info("成功扫描到 {} 个MCP工具方法", toolInfos.size());
        } catch (Exception e) {
            logger.error("扫描MCP工具失败", e);
        }
        
        return toolInfos;
    }
    
    /**
     * MCP工具信息类，用于存储工具的详细信息
     */
    public static class McpToolInfo {
        private String name;
        private String description;
        private String className;
        private String beanName;
        private List<String> parameters;
        private List<Map<String, String>> paramDetails; // 存储参数名称、类型和描述
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getBeanName() { return beanName; }
        public void setBeanName(String beanName) { this.beanName = beanName; }
        public List<String> getParameters() { return parameters; }
        public void setParameters(List<String> parameters) { this.parameters = parameters; }
        public List<Map<String, String>> getParamDetails() { return paramDetails; }
        public void setParamDetails(List<Map<String, String>> paramDetails) { this.paramDetails = paramDetails; }
    }
    
    /**
     * 测试MCP工具的接口，支持传入参数并真正调用工具方法
     */
    @PostMapping("/test-tool-connection")
    @ResponseBody
    public Map<String, Object> testToolConnection(@RequestParam String toolName, @RequestParam Map<String, String> allParams) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 扫描工具
            List<McpToolInfo> toolInfos = scanMcpTools();
            
            // 查找指定的工具
            McpToolInfo targetTool = null;
            for (McpToolInfo toolInfo : toolInfos) {
                if (toolInfo.getName().equals(toolName)) {
                    targetTool = toolInfo;
                    break;
                }
            }
            
            if (targetTool == null) {
                result.put("success", false);
                result.put("message", "测试失败！未找到工具" + toolName + "。");
                logger.warn("测试工具失败: 未找到工具 {}", toolName);
                return result;
            }
            
            try {
                // 获取Bean和方法
                Object bean = applicationContext.getBean(targetTool.getBeanName());
                Class<?> beanClass = bean.getClass();
                
                // 如果是代理类，获取原始类
                if (beanClass.getName().contains("$") && !beanClass.getName().contains("EnhancerBySpringCGLIB")) {
                    beanClass = beanClass.getSuperclass();
                }
                
                // 查找方法
                Method[] methods = beanClass.getDeclaredMethods();
                Method targetMethod = null;
                for (Method method : methods) {
                    if (method.getName().equals(toolName) && method.isAnnotationPresent(McpTool.class)) {
                        targetMethod = method;
                        break;
                    }
                }
                
                if (targetMethod == null) {
                    result.put("success", false);
                    result.put("message", "测试失败！在Bean中未找到方法" + toolName + "。");
                    logger.warn("测试工具失败: 在Bean中未找到方法 {}", toolName);
                    return result;
                }
                
                // 解析参数
                List<Object> convertedParams = new ArrayList<>();
                Class<?>[] paramTypes = targetMethod.getParameterTypes();
                List<Map<String, String>> paramDetails = targetTool.getParamDetails();
                
                // 确保参数可访问
                targetMethod.setAccessible(true);
                
                // 如果没有参数，直接调用
                if (paramTypes.length == 0) {
                    Object methodResult = targetMethod.invoke(bean);
                    result.put("success", true);
                    result.put("message", "测试成功！" + toolName + "方法调用结果: " + (methodResult != null ? methodResult.toString() : "null"));
                    logger.info("测试工具成功: {}，结果: {}", toolName, methodResult);
                    return result;
                }
                
                // 处理有参数的情况
                for (int i = 0; i < paramTypes.length; i++) {
                    String paramName = "param" + i;
                    String paramValue = null;
                    
                    // 尝试从请求参数中获取值
                    if (paramDetails != null && i < paramDetails.size()) {
                        paramName = paramDetails.get(i).get("name");
                    }
                    
                    // 查找对应的参数值
                    if (allParams.containsKey(paramName)) {
                        paramValue = allParams.get(paramName);
                    } else {
                        // 检查是否有前缀为toolName的参数
                        String toolSpecificParamName = toolName + "." + paramName;
                        if (allParams.containsKey(toolSpecificParamName)) {
                            paramValue = allParams.get(toolSpecificParamName);
                        }
                    }
                    
                    // 类型转换
                    if (paramValue != null && !paramValue.isEmpty()) {
                        convertedParams.add(convertParam(paramTypes[i], paramValue));
                    } else {
                        // 如果没有提供参数值，使用默认值
                        convertedParams.add(getDefaultValue(paramTypes[i]));
                    }
                }
                
                // 调用方法
                Object methodResult = targetMethod.invoke(bean, convertedParams.toArray());
                
                // 返回结果
                result.put("success", true);
                result.put("message", "测试成功！" + toolName + "方法调用结果: " + (methodResult != null ? methodResult.toString() : "null"));
                logger.info("测试工具成功: {}，结果: {}", toolName, methodResult);
            } catch (Exception e) {
                // 捕获调用过程中的异常
                result.put("success", false);
                result.put("message", "调用工具方法失败：" + e.getCause().getMessage());
                logger.error("调用工具方法异常", e);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试异常：" + e.getMessage());
            logger.error("测试工具异常", e);
        }
        
        return result;
    }
    
    /**
     * 将字符串参数转换为目标类型
     */
    private Object convertParam(Class<?> paramType, String paramValue) {
        if (paramType == String.class) {
            return paramValue;
        } else if (paramType == Integer.class || paramType == int.class) {
            return Integer.parseInt(paramValue);
        } else if (paramType == Long.class || paramType == long.class) {
            return Long.parseLong(paramValue);
        } else if (paramType == Double.class || paramType == double.class) {
            return Double.parseDouble(paramValue);
        } else if (paramType == Boolean.class || paramType == boolean.class) {
            return Boolean.parseBoolean(paramValue);
        } else if (paramType == Float.class || paramType == float.class) {
            return Float.parseFloat(paramValue);
        } else if (paramType == Short.class || paramType == short.class) {
            return Short.parseShort(paramValue);
        } else if (paramType == Byte.class || paramType == byte.class) {
            return Byte.parseByte(paramValue);
        } else if (paramType.isEnum()) {
            // 尝试转换为枚举类型
            try {
                return Enum.valueOf((Class<? extends Enum>)paramType, paramValue);
            } catch (Exception e) {
                throw new IllegalArgumentException("无法将值" + paramValue + "转换为枚举类型" + paramType.getName());
            }
        }
        // 对于其他类型，尝试返回字符串值或默认值
        return paramValue;
    }
    
    /**
     * 获取类型的默认值
     */
    private Object getDefaultValue(Class<?> paramType) {
        if (paramType.isPrimitive()) {
            if (paramType == int.class) return 0;
            if (paramType == long.class) return 0L;
            if (paramType == double.class) return 0.0;
            if (paramType == boolean.class) return false;
            if (paramType == float.class) return 0.0f;
            if (paramType == short.class) return (short)0;
            if (paramType == byte.class) return (byte)0;
            if (paramType == char.class) return '\0';
        }
        return null;
    }
}