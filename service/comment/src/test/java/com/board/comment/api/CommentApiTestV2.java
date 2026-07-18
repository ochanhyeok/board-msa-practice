package com.board.comment.api;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import com.board.comment.service.request.CommentCreateRequestV2;
import com.board.comment.service.response.CommentPageResponse;
import com.board.comment.service.response.CommentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentApiTestV2 {

	RestClient restClient = RestClient.create("http://localhost:9001");

	@Test
	void create() {
		CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
		CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
		CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

		System.out.println("response1.getCommentId() = " + response1.getPath());
		System.out.println("response1.getCommentId() = " + response1.getCommentId());
		System.out.println("\tresponse2.getCommentId() = " + response2.getPath());
		System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
		System.out.println("\t\tresponse3.getCommentId() = " + response3.getPath());
		System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

		/**
		 * response1.getCommentId() = 00006
		 * response1.getCommentId() = 336808450646204416
		 * 	response2.getCommentId() = 0000600000
		 * 	response2.getCommentId() = 336808450944000000
		 * 		response3.getCommentId() = 000060000000000
		 * 		response3.getCommentId() = 336808451015303168
		 */
	}

	CommentResponse create(CommentCreateRequestV2 request) {
		return restClient.post()
			.uri("/v2/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}

	@Test
	void read() {
		CommentResponse response = restClient.get()
			.uri("/v2/comments/{commentId}", 336808450646204416L)
			.retrieve()
			.body(CommentResponse.class);
		System.out.println("response = " + response);
	}

	@Test
	void delete() {
		restClient.delete()
			.uri("/v2/comments/{commentId}", 336808450646204416L)
			.retrieve();
	}

	@Getter
	@AllArgsConstructor
	public static class CommentCreateRequestV2 {
		private Long articleId;
		private String content;
		private String parentPath;
		private Long writerId;
	}
}
