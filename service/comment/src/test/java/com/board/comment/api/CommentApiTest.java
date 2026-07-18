package com.board.comment.api;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import com.board.comment.service.response.CommentPageResponse;
import com.board.comment.service.response.CommentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentApiTest {

	RestClient restClient = RestClient.create("http://localhost:9001");

	@Test
	void create() {
		CommentResponse response1 = createComment(new CommentCreateRequest(1L, "myComment1", null, 1l));
		CommentResponse response2 = createComment(new CommentCreateRequest(1L, "myComment2", response1.getCommentId(), 1l));
		CommentResponse response3 = createComment(new CommentCreateRequest(1L, "myComment3", response1.getCommentId(), 1l));

		System.out.println("commentId=%s".formatted(response1.getCommentId()));
		System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
		System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));

		// commentId=336402169020342272
		// commentId=336402169540435968
		// commentId=336402169645293568
	}

	CommentResponse createComment(CommentCreateRequest request) {
		return restClient.post()
			.uri("/v1/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}

	@Test
	void read() {
		CommentResponse response = restClient.get()
			.uri("/v1/comments/{commentId}", 336402169020342272L)
			.retrieve()
			.body(CommentResponse.class);

		System.out.println("response = " + response);
	}

	@Test
	void delete() {
		// commentId=336402169020342272
		// commentId=336402169540435968
		// commentId=336402169645293568
		restClient.delete()
			.uri("/v1/comments/{commentId}", 336402169645293568L)
			.retrieve()
			.toBodilessEntity();
	}

	@Test
	void readAll() {
		CommentPageResponse response = restClient.get()
			.uri("/v1/comments?articleId=1&page=1&pageSize=10")
			.retrieve()
			.body(CommentPageResponse.class);

		System.out.println("response.getCommentCount() = " + response.getCommentCount());
		for (CommentResponse comment : response.getComments()) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		/**
		 * comment.getCommentId() = 336401423488585728
		 * comment.getCommentId() = 336407565185437696
		 * 	comment.getCommentId() = 336407565231575043
		 * comment.getCommentId() = 336407565185437697
		 * 	comment.getCommentId() = 336407565231575045
		 * comment.getCommentId() = 336407565185437698
		 * 	comment.getCommentId() = 336407565231575042
		 * comment.getCommentId() = 336407565185437699
		 * 	comment.getCommentId() = 336407565231575047
		 * comment.getCommentId() = 336407565189632000
		 */
	}

	@Test
	void readAllInfiniteScroll() {
		List<CommentResponse> response1 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("firstPage");
		for (CommentResponse comment : response1) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		Long lastParentCommentId = response1.getLast().getParentCommentId();
		Long lastCommentId = response1.getLast().getCommentId();

		List<CommentResponse> response2 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
				.formatted(lastParentCommentId, lastCommentId ))
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("SecondPage");
		for (CommentResponse comment : response2) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}
	}

	@Getter
	@AllArgsConstructor
	public static class CommentCreateRequest {
		private Long articleId;
		private String content;
		private Long parentCommentId;
		private Long writerId;
	}
}
