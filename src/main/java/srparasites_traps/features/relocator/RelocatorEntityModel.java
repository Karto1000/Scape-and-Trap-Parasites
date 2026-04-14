package srparasites_traps.features.relocator;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelNak;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class RelocatorEntityModel extends ModelNak {
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        /* Animation code copied from SRP tweaked to make it work with a Entity that does not inherit from EntityParasiteBase */

        RelocatorEntity relocator = (RelocatorEntity) entityIn;

        if (relocator.getEntityState() == RelocatorEntityState.EMERGING) {
            float f1 = MathHelper.cos(ageInTicks * 0.11095986F) * 0.16429871F;
            float f2 = -1.0F * MathHelper.cos(ageInTicks * 0.13986F) * 0.17429872F;
            float f3 = MathHelper.cos(ageInTicks * 0.0886F) * 0.1472F;
            this.jointLA.rotateAngleX = f1;
            this.jointLA.rotateAngleY = f1 * 0.3F;
            this.jointLA_1.rotateAngleZ = f1;
            this.jointLA_2.rotateAngleZ = 0.0F;
            this.jointLA_3.rotateAngleZ = f1;
            this.jointLA_5.rotateAngleZ = f1;
            this.jointLA_7.rotateAngleZ = f1;
            this.jointA.rotateAngleX = f2;
            this.jointA.rotateAngleY = f2 * -0.2333F;
            this.jointA_1.rotateAngleZ = f2;
            this.jointA_2.rotateAngleZ = 0.0F;
            this.jointA_3.rotateAngleZ = f2;
            this.jointA_5.rotateAngleZ = f2;
            this.jointA_7.rotateAngleZ = f2;
            this.jointL.rotateAngleX = f3;
            this.jointL.rotateAngleY = f3 * 0.5F;
            this.jointL_1.rotateAngleZ = f3;
            this.jointL_2.rotateAngleZ = 0.0F;
            this.jointL_3.rotateAngleZ = f3;
            this.jointL_5.rotateAngleZ = f3;
            this.jointL_7.rotateAngleZ = f3;
        } else {
            float f1 = MathHelper.cos(ageInTicks * 0.1586F) * 0.16429871F;
            float f2 = -1.0F * MathHelper.cos(ageInTicks * 0.196F) * 0.17429872F;
            float f3 = MathHelper.cos(ageInTicks * 0.1716F) * 0.1472F;
            this.jointLA.rotateAngleX = 0.5F + f1;
            this.jointLA.rotateAngleY = f1 * 0.3F;
            this.jointLA_1.rotateAngleZ = -0.3F + f1;
            this.jointLA_2.rotateAngleZ = -0.6F;
            this.jointLA_3.rotateAngleZ = -1.0F + f1;
            this.jointLA_5.rotateAngleZ = f1;
            this.jointLA_7.rotateAngleZ = f1;
            this.jointA.rotateAngleX = 0.8F + f2;
            this.jointA.rotateAngleY = f2 * -0.2333F;
            this.jointA_1.rotateAngleZ = -0.3F + f2;
            this.jointA_2.rotateAngleZ = -0.6F;
            this.jointA_3.rotateAngleZ = f2;
            this.jointA_5.rotateAngleZ = f2;
            this.jointA_7.rotateAngleZ = f2;
            this.jointL.rotateAngleX = 0.5F + f3;
            this.jointL.rotateAngleY = f3 * 0.5F;
            this.jointL_1.rotateAngleZ = -0.3F + f3;
            this.jointL_2.rotateAngleZ = -0.4F;
            this.jointL_3.rotateAngleZ = -0.1F + f3;
            this.jointL_5.rotateAngleZ = f3;
            this.jointL_7.rotateAngleZ = f3;
        }
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        /* Code also from ModelNak */
        RelocatorEntity relocator = (RelocatorEntity) entitylivingbaseIn;
        double emergeTime = relocator.getCurrentEmergeTime();

        if (emergeTime >= 0.0F) {
            this.mainbody.offsetY = (relocator.getEyeHeight() + 1) * (float) emergeTime;
            this.mainbody.offsetX = partialTickTime * 0.091F;
            this.mainbody.offsetZ = partialTickTime * 0.092F;
        } else {
            this.mainbody.offsetY = 0.0F;
            this.mainbody.offsetX = 0.0F;
            this.mainbody.offsetZ = 0.0F;
        }

    }
}
