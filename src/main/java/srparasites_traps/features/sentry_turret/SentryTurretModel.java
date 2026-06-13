package srparasites_traps.features.sentry_turret;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelUnvo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import srparasites_traps.config.ForgeConfigHandler;

public class SentryTurretModel extends ModelUnvo {
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        /* Animation code copied from SRP tweaked to make it work with a Entity that does not inherit from EntityParasiteBase */

        SentryTurretEntity sentryTurret = (SentryTurretEntity) entityIn;

        this.tacleJoint1.rotateAngleX = 0.0F;
        this.tacleJoint1.rotateAngleY = 0.0F;
        this.tacleJoint2.rotateAngleX = 0.0F;
        this.tacleJoint2.rotateAngleY = 0.0F;
        this.tacleJoint3.rotateAngleX = 0.0F;
        this.tacleJoint3.rotateAngleY = 0.0F;
        this.tacleJoint4.rotateAngleX = 0.0F;
        this.tacleJoint4.rotateAngleY = 0.0F;
        this.tacleJoint5.rotateAngleX = 0.0F;
        this.tacleJoint5.rotateAngleY = 0.0F;
        this.tacleJoint6.rotateAngleX = 0.0F;
        this.tacleJoint6.rotateAngleY = 0.0F;
        this.tacleJoint7.rotateAngleX = 0.0F;
        this.tacleJoint7.rotateAngleY = 0.0F;
        this.tacleJoint9.rotateAngleX = 0.0F;
        this.tacleJoint9.rotateAngleY = 0.0F;
        this.tacleJoint10.rotateAngleX = 0.0F;
        this.tacleJoint10.rotateAngleY = 0.0F;
        this.jointULT.rotateAngleY = 0.0F;
        this.jointULT1.rotateAngleY = 0.0F;
        this.jointULT2.rotateAngleY = 0.0F;
        this.jointULT.rotateAngleX = 0.0F;
        this.jointULT1.rotateAngleX = 0.0F;
        this.jointULT2.rotateAngleX = 0.0F;
        this.jointDLT.rotateAngleY = 0.0F;
        this.jointDLT1.rotateAngleY = 0.0F;
        this.jointDLT2.rotateAngleY = 0.0F;
        this.jointDLT.rotateAngleX = 0.0F;
        this.jointDLT1.rotateAngleX = 0.0F;
        this.jointDLT2.rotateAngleX = 0.0F;
        this.jointURT.rotateAngleY = 0.0F;
        this.jointURT1.rotateAngleY = 0.0F;
        this.jointURT2.rotateAngleY = 0.0F;
        this.jointURT.rotateAngleX = 0.0F;
        this.jointURT1.rotateAngleX = 0.0F;
        this.jointURT2.rotateAngleX = 0.0F;
        this.jointDRT.rotateAngleY = 0.0F;
        this.jointDRT1.rotateAngleY = 0.0F;
        this.jointDRT2.rotateAngleY = 0.0F;
        this.jointDRT.rotateAngleX = 0.0F;
        this.jointDRT1.rotateAngleX = 0.0F;
        this.jointDRT2.rotateAngleX = 0.0F;
        this.jointMRT.rotateAngleY = 0.0F;
        this.jointMRT1.rotateAngleY = 0.0F;
        this.jointMRT2.rotateAngleY = 0.0F;
        this.jointMRT.rotateAngleX = 0.0F;
        this.jointMRT1.rotateAngleX = 0.0F;
        this.jointMRT2.rotateAngleX = 0.0F;
        this.jointMLT.rotateAngleY = 0.0F;
        this.jointMLT1.rotateAngleY = 0.0F;
        this.jointMLT2.rotateAngleY = 0.0F;
        this.jointMLT.rotateAngleX = 0.0F;
        this.jointMLT1.rotateAngleX = 0.0F;
        this.jointMLT2.rotateAngleX = 0.0F;
        this.jointULM.rotateAngleX = 0.0F;
        this.jointURM.rotateAngleX = 0.0F;
        this.jointDLM.rotateAngleX = 0.0F;
        this.jointDRM.rotateAngleX = 0.0F;
        this.jointUM.rotateAngleX = 0.0F;
        this.jointDM.rotateAngleX = 0.0F;
        this.jointLM.rotateAngleY = 0.0F;
        this.jointRM.rotateAngleY = 0.0F;
        this.jointH.rotateAngleX = headPitch * 0.016F;
        this.jointH.rotateAngleY = netHeadYaw * -0.016F;

        SentryTurretEntityState state = sentryTurret.getEntityState();
        long ticksSinceTargetLost = (sentryTurret.world.getTotalWorldTime() - sentryTurret.getTicksWhenTargetLost());

        if (state == SentryTurretEntityState.ATTACKING || ticksSinceTargetLost <= ForgeConfigHandler.sentry.DEFAULT_ATTACK_DELAY) {
            float f1a = 0.3F * MathHelper.sin(ageInTicks * 0.021688F) * 0.006F;
            float f2a = -0.6F * MathHelper.sin(ageInTicks * 0.083515F) * 0.006F;
            this.tacleJoint1.rotateAngleX = f1a;
            this.tacleJoint1.rotateAngleY = f2a;
            this.tacleJoint2.rotateAngleX = f1a;
            this.tacleJoint2.rotateAngleY = f2a;
            this.tacleJoint3.rotateAngleX = f1a;
            this.tacleJoint3.rotateAngleY = f2a;
            this.tacleJoint4.rotateAngleX = f1a;
            this.tacleJoint4.rotateAngleY = f2a;
            this.tacleJoint5.rotateAngleX = f1a;
            this.tacleJoint5.rotateAngleY = f2a;
            this.tacleJoint6.rotateAngleX = f1a;
            this.tacleJoint6.rotateAngleY = f2a;
            this.tacleJoint7.rotateAngleX = f1a;
            this.tacleJoint7.rotateAngleY = f2a;
            this.tacleJoint9.rotateAngleX = f1a;
            this.tacleJoint9.rotateAngleY = f2a;
            this.tacleJoint10.rotateAngleX = f1a;
            this.tacleJoint10.rotateAngleY = f2a;
            float f11a = 0.3F * MathHelper.sin(ageInTicks * 0.15F) * 0.05F;
            float f22a = 0.6F * MathHelper.sin(ageInTicks * 0.07F) * 0.1F;
            float f33a = 0.6F * MathHelper.sin(ageInTicks * 0.1F) * 0.05F;
            this.jointULT.rotateAngleY = -1.0F * f11a;
            this.jointULT1.rotateAngleY = -1.0F * f11a;
            this.jointULT2.rotateAngleY = -1.0F * f11a;
            this.jointULT.rotateAngleX = 0.0F + f33a;
            this.jointULT1.rotateAngleX = 0.0F + f33a;
            this.jointULT2.rotateAngleX = 0.0F + f33a;
            this.jointURT.rotateAngleY = f11a;
            this.jointURT1.rotateAngleY = f11a;
            this.jointURT2.rotateAngleY = f11a;
            this.jointURT.rotateAngleX = 0.0F + f33a;
            this.jointURT1.rotateAngleX = 0.0F + f33a;
            this.jointURT2.rotateAngleX = 0.0F + f33a;
            this.jointMLT.rotateAngleY = f11a;
            this.jointMLT1.rotateAngleY = f11a;
            this.jointMLT2.rotateAngleY = f11a;
            this.jointMLT.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointMLT1.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointMLT2.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointMRT.rotateAngleY = f11a;
            this.jointMRT1.rotateAngleY = f11a;
            this.jointMRT2.rotateAngleY = f11a;
            this.jointMRT.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointMRT1.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointMRT2.rotateAngleX = 0.0F + -1.0F * f33a;
            this.jointDLT.rotateAngleY = -1.0F * f11a;
            this.jointDLT1.rotateAngleY = -1.0F * f11a;
            this.jointDLT2.rotateAngleY = -1.0F * f11a;
            this.jointDLT.rotateAngleX = 0.0F + f33a;
            this.jointDLT1.rotateAngleX = 0.0F + f33a;
            this.jointDLT2.rotateAngleX = 0.0F + f33a;
            this.jointDRT.rotateAngleY = -1.0F * f11a;
            this.jointDRT1.rotateAngleY = -1.0F * f11a;
            this.jointDRT2.rotateAngleY = -1.0F * f11a;
            this.jointDRT.rotateAngleX = 0.0F + f33a;
            this.jointDRT1.rotateAngleX = 0.0F + f33a;
            this.jointDRT2.rotateAngleX = 0.0F + f33a;
            this.jointULM.rotateAngleX = -1.0F * f22a;
            this.jointURM.rotateAngleX = -1.0F * f22a;
            this.jointDLM.rotateAngleX = f22a;
            this.jointDRM.rotateAngleX = f22a;
            this.jointUM.rotateAngleX = -0.5F + f11a;
            this.jointDM.rotateAngleX = 0.5F + f11a;
            this.jointLM.rotateAngleY = 0.5F + f11a;
            this.jointRM.rotateAngleY = -0.5F + f11a;
        } else if (state == SentryTurretEntityState.IDLE) {
            float f1i = 0.3F * MathHelper.sin(ageInTicks * 0.051688F) * 0.011F;
            float f2i = -0.6F * MathHelper.sin(ageInTicks * 0.13515F) * 0.011F;
            this.tacleJoint1.rotateAngleX = f1i;
            this.tacleJoint1.rotateAngleY = f2i;
            this.tacleJoint2.rotateAngleX = f1i;
            this.tacleJoint2.rotateAngleY = f2i;
            this.tacleJoint3.rotateAngleX = f1i;
            this.tacleJoint3.rotateAngleY = f2i;
            this.tacleJoint4.rotateAngleX = f1i;
            this.tacleJoint4.rotateAngleY = f2i;
            this.tacleJoint5.rotateAngleX = f1i;
            this.tacleJoint5.rotateAngleY = f2i;
            this.tacleJoint6.rotateAngleX = f1i;
            this.tacleJoint6.rotateAngleY = f2i;
            this.tacleJoint7.rotateAngleX = f1i;
            this.tacleJoint7.rotateAngleY = f2i;
            this.tacleJoint9.rotateAngleX = f1i;
            this.tacleJoint9.rotateAngleY = f2i;
            this.tacleJoint10.rotateAngleX = f1i;
            this.tacleJoint10.rotateAngleY = f2i;
            float f11i = 0.3F * MathHelper.sin(ageInTicks * 0.15F) * 0.1F;
            float f22i = 0.6F * MathHelper.sin(ageInTicks * 0.07F) * 0.2F;
            float f33i = 0.6F * MathHelper.sin(ageInTicks * 0.1F) * 0.1F;
            this.jointULT.rotateAngleY = -1.0F * f11i;
            this.jointULT1.rotateAngleY = -1.0F * f11i;
            this.jointULT2.rotateAngleY = -1.0F * f11i;
            this.jointULT.rotateAngleX = 0.5F + f33i;
            this.jointULT1.rotateAngleX = 0.3F + f33i;
            this.jointULT2.rotateAngleX = 0.1F + f33i;
            this.jointURT.rotateAngleY = f11i;
            this.jointURT1.rotateAngleY = f11i;
            this.jointURT2.rotateAngleY = f11i;
            this.jointURT.rotateAngleX = 0.5F + f33i;
            this.jointURT1.rotateAngleX = 0.3F + f33i;
            this.jointURT2.rotateAngleX = 0.1F + f33i;
            this.jointMLT.rotateAngleY = f11i;
            this.jointMLT1.rotateAngleY = f11i;
            this.jointMLT2.rotateAngleY = f11i;
            this.jointMLT.rotateAngleX = 0.5F + -1.0F * f33i;
            this.jointMLT1.rotateAngleX = 0.3F + -1.0F * f33i;
            this.jointMLT2.rotateAngleX = 0.1F + -1.0F * f33i;
            this.jointMRT.rotateAngleY = f11i;
            this.jointMRT1.rotateAngleY = f11i;
            this.jointMRT2.rotateAngleY = f11i;
            this.jointMRT.rotateAngleX = 0.5F + -1.0F * f33i;
            this.jointMRT1.rotateAngleX = 0.3F + -1.0F * f33i;
            this.jointMRT2.rotateAngleX = 0.1F + -1.0F * f33i;
            this.jointDLT.rotateAngleY = -1.0F * f11i;
            this.jointDLT1.rotateAngleY = -1.0F * f11i;
            this.jointDLT2.rotateAngleY = -1.0F * f11i;
            this.jointDLT.rotateAngleX = 0.5F + f33i;
            this.jointDLT1.rotateAngleX = 0.3F + f33i;
            this.jointDLT2.rotateAngleX = 0.1F + f33i;
            this.jointDRT.rotateAngleY = -1.0F * f11i;
            this.jointDRT1.rotateAngleY = -1.0F * f11i;
            this.jointDRT2.rotateAngleY = -1.0F * f11i;
            this.jointDRT.rotateAngleX = 0.5F + f33i;
            this.jointDRT1.rotateAngleX = 0.3F + f33i;
            this.jointDRT2.rotateAngleX = 0.1F + f33i;
            this.jointULM.rotateAngleX = -1.0F * f22i;
            this.jointURM.rotateAngleX = -1.0F * f22i;
            this.jointDLM.rotateAngleX = f22i;
            this.jointDRM.rotateAngleX = f22i;
            this.jointUM.rotateAngleX = 0.5F + f11i;
            this.jointDM.rotateAngleX = -0.5F + f11i;
            this.jointLM.rotateAngleY = -0.5F + f11i;
            this.jointRM.rotateAngleY = 0.5F + f11i;
        } else if (state == SentryTurretEntityState.EMERGING) {
            this.tacleJoint1.rotateAngleX = 0.0F;
            this.tacleJoint2.rotateAngleX = -0.7F;
            this.tacleJoint3.rotateAngleX = 0.7F;
            this.tacleJoint4.rotateAngleX = 0.5F;
            this.tacleJoint5.rotateAngleX = 0.0F;
            this.tacleJoint6.rotateAngleX = 0.2F;
            this.tacleJoint7.rotateAngleX = -0.5F;
            this.tacleJoint9.rotateAngleX = -0.5F;
            this.tacleJoint10.rotateAngleX = -0.5F;
            this.jointH.rotateAngleX = -0.7F;
            this.jointUM.rotateAngleX = 0.5F;
            this.jointDM.rotateAngleX = -0.5F;
            this.jointLM.rotateAngleY = -0.5F;
            this.jointRM.rotateAngleY = 0.5F;
            this.jointDLT.rotateAngleY = -0.5F;
            this.jointDRT.rotateAngleY = 0.5F;
            this.jointMLT.rotateAngleY = -0.5F;
            this.jointMRT.rotateAngleY = 0.5F;
            this.jointULT.rotateAngleY = -0.3F;
            this.jointURT.rotateAngleY = 0.3F;
            this.jointULT.rotateAngleX = 0.6F;
            this.jointURT.rotateAngleX = 0.6F;
        }
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        /* Code also from ModelUnvo */
        SentryTurretEntity sentry = (SentryTurretEntity) entitylivingbaseIn;
        double emergeTime = sentry.getCurrentEmergeTime();

        if (emergeTime >= 0.0F) {
            this.mainbody.offsetY = (sentry.getEyeHeight() + 1) * (float) emergeTime;
            this.mainbody.offsetX = partialTickTime * 0.091F;
            this.mainbody.offsetZ = partialTickTime * 0.092F;
        } else {
            this.mainbody.offsetY = 0.0F;
            this.mainbody.offsetX = 0.0F;
            this.mainbody.offsetZ = 0.0F;
        }

    }
}
