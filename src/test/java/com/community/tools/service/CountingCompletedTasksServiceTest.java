package com.community.tools.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.community.tools.service.github.GitHubConnectService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

class CountingCompletedTasksServiceTest {


  @Test
  void getCountedCompletedTasks() throws IOException {


    GHPullRequest pr = mock(GHPullRequest.class);

    Collection<GHLabel> colLabel = new ArrayList<>();
    GHLabel ghLabel = mock(GHLabel.class);
    colLabel.add(ghLabel);
    when(ghLabel.getName()).thenReturn("done");
    when(pr.getLabels()).thenReturn(colLabel);

    GHUser user = mock(GHUser.class);

    when(user.getLogin()).thenReturn("roman");
    when(pr.getUser()).thenReturn(user);

    when(pr.getTitle()).thenReturn("exercise_1");
    List<GHPullRequest> fakeReq = new ArrayList<>();
    fakeReq.add(pr);

    GitHubConnectService service = mock(GitHubConnectService.class);
    GHRepository rep = mock(GHRepository.class);
    when(service.getGitHubRepository()).thenReturn(rep);
    when(rep.getPullRequests(GHIssueState.CLOSED)).thenReturn(fakeReq);

    CountingCompletedTasksService cct = new CountingCompletedTasksService(service);

    Map<String, List<String>> map = cct.getCountedCompletedTasks();

    assertEquals(map.size(), 1);
    for (Entry<String, List<String>> list : map.entrySet()) {
      assertEquals(list.getKey(), "roman");
      assertEquals(list.getValue().size(), 1);
      assertEquals(list.getValue().iterator().next(), "exercise_1");
    }
  }
}