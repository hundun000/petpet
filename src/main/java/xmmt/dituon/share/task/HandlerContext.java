package xmmt.dituon.share.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import xmmt.dituon.share.task.provider.IImageProvider;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class HandlerContext {
    Map<ImageProviderType, IImageProvider> imageProviderMap;
}
