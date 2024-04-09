package com.CloseConnect.closeconnect.repository.token;

import com.CloseConnect.closeconnect.entity.token.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Token findByToken(String token);
}
