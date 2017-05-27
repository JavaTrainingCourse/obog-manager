/*
 * Copyright (c) 2017 mikan
 */

package com.github.javatrainingcourse.obogmanager.domain.service;

import com.github.javatrainingcourse.obogmanager.domain.model.Convocation;
import com.github.javatrainingcourse.obogmanager.domain.repository.ConvocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * OB/OG会に関する操作を提供します。
 *
 * @author mikan
 * @since 0.1
 */
@Service
public class ConvocationService {

    private final ConvocationRepository convocationRepository;

    @Autowired
    public ConvocationService(ConvocationRepository convocationRepository, ResourceLoader resourceLoader) {
        this.convocationRepository = convocationRepository;
    }

    /**
     * 最新のイベント招集を取得します。
     *
     * @return 最新のイベント招集
     * @throws IllegalStateException イベント招集がまだ登録されていない場合
     * @throws DataAccessException   操作が失敗した場合
     */
    public Convocation getLatestConvocation() {
        return convocationRepository.findAll(new Sort(Sort.Direction.ASC, "targetDate")).stream().findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * 新しいイベント招集を登録します。
     *
     * @param convocation イベント招集
     * @throws DataAccessException 操作が失敗した場合
     */
    public void register(Convocation convocation) {
        convocation.setCreatedDate(new Date());
        convocation.setLastUpdateDate(new Date());
        convocationRepository.saveAndFlush(convocation);
    }
}
