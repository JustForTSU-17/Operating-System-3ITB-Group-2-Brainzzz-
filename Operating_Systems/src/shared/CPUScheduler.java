package shared;

import java.util.List;

public interface CPUScheduler {
    CPUResult schedule(List<CPUProcess> jobs);
}

