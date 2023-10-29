package com.ruyi.mallchat.sensitive;

import com.ruyi.mallchat.common.utils.sensitiveWord.IWordDeny;
import com.ruyi.mallchat.sensitive.dao.SensitiveWordDao;
import com.ruyi.mallchat.sensitive.domain.SensitiveWord;
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
