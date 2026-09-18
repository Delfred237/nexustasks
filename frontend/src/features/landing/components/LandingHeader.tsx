import { Link } from "react-router-dom";
import { CheckSquare } from "lucide-react";
import { Button } from "@/components/ui/button";

export function LandingHeader() {
  return (
    <header className="sticky top-0 z-40 w-full border-b bg-background/80 backdrop-blur supports-backdrop-filter:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
            <CheckSquare className="h-4 w-4 text-primary-foreground" />
          </div>
          <span className="text-lg font-bold">NexusTasks</span>
        </Link>

        <nav className="hidden md:flex items-center gap-8 text-sm font-medium">
          <a
            href="#features"
            className="text-muted-foreground hover:text-foreground transition-colors"
          >
            Features
          </a>
          <a
            href="#how-it-works"
            className="text-muted-foreground hover:text-foreground transition-colors"
          >
            How it works
          </a>
          <a
            href="#pricing"
            className="text-muted-foreground hover:text-foreground transition-colors"
          >
            Pricing
          </a>
        </nav>

        <div className="flex items-center gap-3">
          <Button
            size="lg"
            variant="default"
            render={(props) => <Link to="/register" {...props} />}
          >
            Sign Up
          </Button>
          <Button
            size="lg"
            variant="outline"
            render={(props) => <Link to="/login" {...props} />}
          >
            Login
          </Button>
        </div>
      </div>
    </header>
  );
}
