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
import java.util.List;

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

    public List<Convocation> getAll() {
        return convocationRepository.findAll(new Sort(Sort.Direction.DESC, "lastUpdateDate"));
    }

    /**
     * 最新のイベント招集を取得します。
     *
     * @return 最新のイベント招集
     * @throws IllegalStateException イベント招集がまだ登録されていない場合
     * @throws DataAccessException   操作が失敗した場合
     */
    public Convocation getLatestConvocation() {
        return convocationRepository.findAll(new Sort(Sort.Direction.DESC, "lastUpdateDate")).stream().findFirst()
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

    /**
     * イベント招集を更新します。
     *
     * @param convocation ベント招集
     * @throws IllegalArgumentException 存在しないイベント招集の場合
     * @throws DataAccessException      操作が失敗した場合
     */
    public void update(Convocation convocation) {
        if (!convocationRepository.exists(convocation.getId())) {
            throw new IllegalArgumentException("No such convocation: " + convocation.getId());
        }
        convocation.setLastUpdateDate(new Date());
        convocationRepository.saveAndFlush(convocation);
    }

    public long countConvocations() {
        return convocationRepository.count();
    }
}
