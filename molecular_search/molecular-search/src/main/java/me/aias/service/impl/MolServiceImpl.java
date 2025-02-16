package me.aias.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import me.aias.domain.MolInfoDto;
import me.aias.service.MolService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分子服务
 * Mol Service
 *
 * @author Calvin
 * @date 2021-12-19
 **/
@Slf4j
@Service
public class MolServiceImpl implements MolService {
    private static final String TEXT_LIST = "text-list.json";
    private Logger logger = LoggerFactory.getLogger(LocalStorageServiceImpl.class);
    private ConcurrentHashMap<Long, MolInfoDto> map;

    public MolServiceImpl() {
        StringBuilder sb = new StringBuilder();
        try {
            String path = System.getProperty("user.dir");
            File file = new File(path, TEXT_LIST);
            BufferedReader br;
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            logger.error("File read error", e);
        }
        String jsonStr = sb.toString();
        if (!StringUtils.isBlank(jsonStr)) {
            map = new Gson().fromJson(jsonStr, new TypeToken<ConcurrentHashMap<Long, MolInfoDto>>() {
            }.getType());
        } else {
            map = new ConcurrentHashMap<>();
        }
    }

    /**
     * 新增文本
     * Add new file
     */
    public void addText(MolInfoDto textInfoDto) {
        map.put(textInfoDto.getId(), textInfoDto);
        saveTextList();
    }

    /**
     * 新增文本
     * Add new file
     */
    public void addTexts(List<MolInfoDto> texts) {
        for (MolInfoDto DNAInfoDto : texts) {
            DNAInfoDto.setFeature(null);
            map.put(DNAInfoDto.getId(), DNAInfoDto);
        }
        saveTextList();
    }

    /**
     * 根据ID查询
     * find file by id
     */
    public MolInfoDto findById(Long id) {
        return map.get(id);
    }

    /**
     * 获取清单
     * get list
     */
    public ConcurrentHashMap<Long, MolInfoDto> getMap() {
        return map;
    }

    /**
     * 保存上传文件列表
     * save text list
     */
    private void saveTextList() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(map);
        try {
            File file = new File(TEXT_LIST);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.print(jsonStr);
        } catch (FileNotFoundException e) {
            logger.error("Storage file not found", e);
        }
    }
}