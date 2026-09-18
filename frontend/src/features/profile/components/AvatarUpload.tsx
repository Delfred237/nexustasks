import { useRef, useState } from "react";
import { Upload, Trash2 } from "lucide-react";
import { toast } from "react-hot-toast";
import { Button } from "@/components/ui/button";
import { profileService } from "../services/profile.service";
import { useAuth } from "@/features/auth/hooks/useAuth";

export function AvatarUpload() {
  const { user, setUser } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleFileChange = async (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validation taille (2MB max)
    if (file.size > 2 * 1024 * 1024) {
      toast.error("File size must be less than 2MB");
      return;
    }

    // Validation type
    const validTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];
    if (!validTypes.includes(file.type)) {
      toast.error("Only JPEG, PNG, GIF, and WebP images are allowed");
      return;
    }

    setIsUploading(true);
    try {
      const response = await profileService.uploadAvatar(file);
      if (user) {
        setUser({ ...user, avatarUrl: response.avatarUrl });
      }
      toast.success("Avatar uploaded successfully");
    } catch {
      toast.error("Failed to upload avatar");
    } finally {
      setIsUploading(false);
    }
  };

  const handleDelete = async () => {
    try {
      await profileService.deleteAvatar();
      if (user) {
        setUser({ ...user, avatarUrl: null });
      }
      toast.success("Avatar removed");
    } catch {
      toast.error("Failed to remove avatar");
    }
  };

  const initials = user
    ? `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase()
    : "?";

  return (
    <div className="flex items-center gap-4">
      {/* Avatar preview */}
      {user?.avatarUrl ? (
        <img
          src={user.avatarUrl}
          alt={`${user.firstName} ${user.lastName}`}
          className="h-20 w-20 rounded-full object-cover"
        />
      ) : (
        <div className="flex h-20 w-20 items-center justify-center rounded-full bg-primary text-primary-foreground text-2xl font-bold">
          {initials}
        </div>
      )}

      {/* Actions */}
      <div className="flex flex-col gap-2">
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileChange}
          accept="image/jpeg,image/png,image/gif,image/webp"
          className="hidden"
        />
        <Button
          variant="outline"
          size="sm"
          onClick={() => fileInputRef.current?.click()}
          disabled={isUploading}
          className="gap-2"
        >
          <Upload className="h-4 w-4" />
          {isUploading ? "Uploading..." : "Upload"}
        </Button>
        {user?.avatarUrl && (
          <Button
            variant="ghost"
            size="sm"
            onClick={handleDelete}
            className="gap-2 text-destructive"
          >
            <Trash2 className="h-4 w-4" />
            Remove
          </Button>
        )}
      </div>
    </div>
  );
}
