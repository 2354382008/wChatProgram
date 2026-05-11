package com.wChartProgram.buss.constroct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public abstract class ConstroctManager {

    private static volatile ConstroctManager INSTANCE;

    private final Map<Class<? extends ConstroctInterface>, Map<String,ConstroctInterface>> classMapMap = new HashMap<>();

    @Autowired
    private List<ConstroctInterface> constroctInterfaceList;

    public ConstroctManager() {
        if (INSTANCE != null){
            log.warn("已经存在实例了，不需要再次创建!");
        }
        INSTANCE = this;
    }

    protected void init(){
        if (CollectionUtils.isEmpty(constroctInterfaceList)){
            log.warn("获取constroctInterfaceList为空!");
        }
        log.info("constroctInterfaceList的size：{}",constroctInterfaceList.size());
        this.constroctInterfaceList.forEach(item ->{
            System.out.println("实现类:"+item.getInterfaceClass().getName());
            Map<String,ConstroctInterface> subMap = this.classMapMap.computeIfAbsent(item.getInterfaceClass(),k -> new HashMap<>());
            subMap.put(item.getClass().getSimpleName(),item);
        });
        List<String> strsList = this.getStrsList();
        log.info("strsList的size：{}",strsList.size());
    }

    /**
     * 获取开启扩展校验的列表
     * @return
     */
    protected abstract List<String> getStrsList();

    public static <T extends ConstroctInterface> void executeVoidAsync(Class<T> targetClass, Consumer<T> execFunction){
        List<T> list = INSTANCE.constroctInterfaceList(targetClass);
        list.forEach(execFunction);
    }

    /**
     * 获取扩展实现类
     * @param targetClass
     * @return
     * @param <T>
     */
    private <T extends ConstroctInterface> List<T> constroctInterfaceList(Class<T> targetClass) {
        List<ConstroctInterface> interfaces = new ArrayList<>(this.classMapMap.get(targetClass).values());
        return interfaces.stream().map(item -> (T) item).collect(Collectors.toList());
    }
}
