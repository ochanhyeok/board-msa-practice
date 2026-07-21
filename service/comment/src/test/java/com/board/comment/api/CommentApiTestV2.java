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

	@Test
	void readAll() {
		CommentPageResponse response = restClient.get()
			.uri("/v2/comments?articleId=1&pageSize=10&page=1")
			.retrieve()
			.body(CommentPageResponse.class);

		System.out.println("response.getCommentCount() = " + response.getCommentCount());
		for (CommentResponse comment : response.getComments()) {
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		/*
		* comment.getCommentId() = 336806347751763968
			comment.getCommentId() = 336807452407607296
			comment.getCommentId() = 336807837233438720
			comment.getCommentId() = 336807837963247616
			comment.getCommentId() = 336807838047133696
			comment.getCommentId() = 336807903419555840
			comment.getCommentId() = 336807903755100160
			comment.getCommentId() = 336807903818014720
			comment.getCommentId() = 336807992317829120
			comment.getCommentId() = 336807992624013312
		* */
	}

	@Test
	void readAllInfiniteScroll() {
		List<CommentResponse> response1 = restClient.get()
			.uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("firstPage");
		for (CommentResponse comment : response1) {
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		String lastPath = response1.getLast().getPath();
		List<CommentResponse> response2 = restClient.get()
			.uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("SecondPage");
		for (CommentResponse comment : response2) {
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}
	}

	@Test
	void countTest() {
		CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my content1", null, 1L));

		Long count1 = restClient.get()
			.uri("/v2/comments/articles/{articleId}/count", 2L)
			.retrieve()
			.body(Long.class);
		System.out.println("count1 = " + count1);

		restClient.delete()
			.uri("/v2/comments/{commentId}", commentResponse.getCommentId())
			.retrieve();

		Long count2 = restClient.get()
			.uri("/v2/comments/articles/{articleId}/count", 2L)
			.retrieve()
			.body(Long.class);
		System.out.println("count2 = " + count2);
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
