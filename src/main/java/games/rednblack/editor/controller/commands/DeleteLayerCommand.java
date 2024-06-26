package games.rednblack.editor.controller.commands;

import games.rednblack.editor.renderer.components.NodeComponent;
import games.rednblack.editor.renderer.components.ZIndexComponent;
import games.rednblack.editor.utils.runtime.SandboxComponentRetriever;
import games.rednblack.editor.view.stage.Sandbox;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CyberJoe on 7/25/2015.
 */
public class DeleteLayerCommand extends TransactiveCommand {
    private static final String CLASS_NAME = "games.rednblack.editor.controller.commands.DeleteLayerCommand";
    public static final String DONE = CLASS_NAME + "DONE";
    public static final String UNDONE = CLASS_NAME + "UNDONE";

    private String layerName;
    private DeleteLayerAtomCommand deleteLayerAtomCommand;

    @Override
    public void transaction() {
        layerName = getNotification().getBody();
        deleteLayerAtomCommand = new DeleteLayerAtomCommand(layerName);
        addInnerCommand(deleteLayerAtomCommand);
        DeleteItemsCommand deleteItemsCommand = new DeleteItemsCommand();
        deleteItemsCommand.setItemsToDelete(getItemsByLayerName(layerName));
        addInnerCommand(deleteItemsCommand);
    }

    @Override
    public void onFinish() {
        facade.sendNotification(DONE, deleteLayerAtomCommand.getLayerIndex());
    }

    @Override
    public void onFinishUndo() {
        facade.sendNotification(UNDONE, layerName);
    }

    public Set<Integer> getItemsByLayerName(String layerName) {
        Set<Integer> result = new HashSet<>();
        int viewingEntity = Sandbox.getInstance().getCurrentViewingEntity();
        NodeComponent nodeComponent = SandboxComponentRetriever.get(viewingEntity, NodeComponent.class);
        for(int i = 0; i < nodeComponent.children.size; i++) {
            int child = nodeComponent.children.get(i);
            ZIndexComponent zIndexComponent = SandboxComponentRetriever.get(child, ZIndexComponent.class);
            if(zIndexComponent.getLayerName().equals(layerName)) {
                result.add(child);
            }
        }

        return result;
    }

}
