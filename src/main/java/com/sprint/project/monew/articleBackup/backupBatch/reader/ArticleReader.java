package com.sprint.project.monew.articleBackup.backupBatch.reader;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.articleBackup.backupBatch.dto.InterestKeywordDto;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleReader implements ItemReader<InterestKeywordDto> {

    private final InterestRepository interestRepository;

    private Iterator<InterestKeywordDto> iterator;


    @Override
    public InterestKeywordDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 관심사 + 키워드 DTO 준비
        List<Interest> interests = interestRepository.findAll();
        List<InterestKeywordDto> list = new ArrayList<>();

        for (Interest interest : interests) {
            if (interest.getKeywords() == null) continue;
            for (String keyword : interest.getKeywords()) {
                list.add(new InterestKeywordDto(interest.getId(), keyword));
            }
        }
        if(iterator == null){
            iterator = list.iterator();

        }

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null; // 끝
        }

    }
}
