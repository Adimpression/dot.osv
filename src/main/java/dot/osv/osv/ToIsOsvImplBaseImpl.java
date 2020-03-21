package dot.osv.osv;

import dot.dependency.dependency.IsDependency;
import dot.osv.input.IsInput;
import dot.osv.output.IsOutput;
import dot.state.state.IsState;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;

import java.util.List;

final public class ToIsOsvImplBaseImpl extends ToIsOsvGrpc.ToIsOsvImplBase {

    private Stopwatch produce = SimonManager.getStopwatch(this.getClass()
            .getName());

    @Override
    public void produce(final NotOsv request, final StreamObserver<IsOsv> responseObserver) {
        final Split start = produce.start();
        try {
            final StringBuilder stringBuilder = new StringBuilder();

            final IsInput isInput = request.getIsInput();

            final IsState isState = isInput.getIsState();

            final List<IsDependency> dependencies = isInput.getIsDependencyList();

            final String fullStateString = isState.getIsValueString();

            stringBuilder.append(fullStateString.substring(fullStateString.lastIndexOf("/") + 1))
                    .append("\n");

            for (IsDependency dependency : dependencies) {
                stringBuilder.append("\t")
                        .append(dependency.getIsOutput()
                                .getIsDid()
                                .getIsOutput()
                                .getIsStringValue())
                        .append("\n");
            }

            final String stateFileName = fullStateString.substring(0,
                    fullStateString.lastIndexOf("/"));

            responseObserver.onNext(IsOsv.newBuilder()
                    .setIsOutput(IsOutput.newBuilder()
                            .setIsOsv(osv.osv.IsOsv.newBuilder()
                                    .setIsOutput(osv.output.IsOutput.newBuilder()
                                            .setIsNameString(stateFileName)
                                            .setIsContentString(stringBuilder.toString())
                                            .build())
                                    .build())
                            .build())
                    .build());

            responseObserver.onCompleted();
        } catch (final Exception e) {
            responseObserver.onError(Status.fromThrowable(e)
                    .asException());
        }
        final Split stop = start.stop();
        System.out.println(produce);
    }
}
