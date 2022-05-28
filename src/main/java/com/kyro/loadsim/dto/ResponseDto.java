package com.kyro.loadsim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.LongSummaryStatistics;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDto {
  private Integer totalTasksSubmitted;
  private Integer totalTasksDone;
  private Integer completedTasks;
  private Integer failedTasks;
  private List<Details> details;
  private LongSummaryStatistics avgTimeTaken;
}
