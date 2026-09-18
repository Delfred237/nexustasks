import { AvatarUpload } from "../components/AvatarUpload";
import { ProfileForm } from "../components/ProfileForm";
import { ChangePasswordForm } from "../components/ChangePasswordForm";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { PageTransition } from "@/components/PageTransition";

export default function ProfilePage() {
  const { user } = useAuth();

  return (
    <PageTransition>
      <div className="space-y-8 max-w-2xl">
        <div>
          <h1 className="text-2xl font-bold">Profile</h1>
          <p className="text-muted-foreground">Manage your account settings</p>
        </div>

        {/* Avatar section */}
        <section className="rounded-lg border p-6">
          <h2 className="text-lg font-semibold mb-4">Avatar</h2>
          <AvatarUpload />
        </section>

        {/* Profile info section */}
        <section className="rounded-lg border p-6">
          <h2 className="text-lg font-semibold mb-4">Personal Information</h2>
          <ProfileForm />
        </section>

        {/* Password section */}
        <section className="rounded-lg border p-6">
          <h2 className="text-lg font-semibold mb-4">Change Password</h2>
          <ChangePasswordForm />
        </section>

        {/* Account info */}
        <section className="rounded-lg border p-6">
          <h2 className="text-lg font-semibold mb-4">Account</h2>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Email</span>
              <span>{user?.email}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Role</span>
              <span>{user?.role}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Email Verified</span>
              <span>{user?.emailVerified ? "✅ Yes" : "❌ No"}</span>
            </div>
          </div>
        </section>
      </div>
    </PageTransition>
  );
}
