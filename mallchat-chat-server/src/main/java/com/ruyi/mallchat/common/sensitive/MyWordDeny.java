package com.ruyi.mallchat.common.sensitive;

import com.ruyi.mallchat.common.common.utils.sensitiveWord.IWordDeny;
import com.ruyi.mallchat.common.sensitive.dao.SensitiveWordDao;
import com.ruyi.mallchat.common.sensitive.domain.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyWordDeny implements IWordDeny {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> deny() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}
