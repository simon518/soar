import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.UnsupportedEncodingException;

/**
 * NodeCacheExample
 * 当节点的数据修改或者删除时，Node Cache能更新它的状态包含最新的改变。
 *
 * 节点创建，节点数据内容变更，不能监听节点删除
 *
 * @author xiuyuhang [xiuyuhang]
 * @since 2018-03-20
 */
public class NodeCacheExample {

    public static void main(String[] args) throws Exception {
        CuratorFramework client = getClient();
        String path = "/p1";
        final NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("监听事件触发");
                System.out.println("重新获得节点内容为：" + new String(nodeCache.getCurrentData().getData()));
            }
        });
        client.setData().forPath(path, "456".getBytes());
        client.setData().forPath(path, "789".getBytes());
        client.setData().forPath(path, "123".getBytes());
        client.setData().forPath(path, "222".getBytes());
        client.setData().forPath(path, "333".getBytes());
        client.setData().forPath(path, "444".getBytes());
        Thread.sleep(5000);

    }

    private static CuratorFramework getClient() throws UnsupportedEncodingException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .authorization("digest", "1xife@F1FXX".getBytes("utf-8"))
                .connectString("10.165.124.48:2181,10.165.124.50:2181,10.165.124.51:2181")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .namespace("demo")
                .build();
        client.start();
        return client;
    }
}
