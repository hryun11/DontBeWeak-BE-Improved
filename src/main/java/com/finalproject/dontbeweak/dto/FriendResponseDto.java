package com.finalproject.dontbeweak.dto;

import com.finalproject.dontbeweak.model.Friend;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendResponseDto {

   @ApiModelProperty(example = "친구 닉네임")
   private String nickname;

   @ApiModelProperty(example = "친구 아이디")
   private String friendname;


   public FriendResponseDto(Friend  friend) {
      this.nickname = friend.getNickname();
      this.friendname = friend.getFriendname();
   }
}
