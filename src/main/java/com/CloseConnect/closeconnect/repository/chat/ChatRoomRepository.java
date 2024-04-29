package com.CloseConnect.closeconnect.repository.chat;

import com.CloseConnect.closeconnect.entity.chat.ChatRoom;
import com.CloseConnect.closeconnect.entity.chat.ChatRoomType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    //참가지목록에 email 포함되어있는 ChatRoomList
    List<ChatRoom> findByParticipantListEmail(String email);

    //삭제된 ChatRoomList
    List<ChatRoom> findByIsDeletedTrue();

    //삭제되지 않은 ChatRoom chatRoomType 으로 검색
    List<ChatRoom> findByChatRoomTypeAndIsDeletedFalse(ChatRoomType chatRoomType);

    // 삭제되지 않고 emails 포함되어있는 ChatRoom chatRoomType 으로 검색 (PRIVATE 일때만 사용 중)
    Optional<ChatRoom> findByChatRoomTypeAndIsDeletedIsFalseAndParticipantListEmailIn(ChatRoomType chatRoomType, List<String> emails);
}
