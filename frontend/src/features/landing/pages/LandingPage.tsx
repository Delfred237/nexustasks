import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function LandingPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <span className="text-2xl font-bold">NexusTasks</span>
          <nav className="flex items-center gap-4">
            <Button
              variant="ghost"
              render={(props) => <Link to="/login" {...props} />}
            >
              Login
            </Button>
            <Button
              variant="default"
              render={(props) => <Link to="/register" {...props} />}
            >
              Sign Up
            </Button>
          </nav>
        </div>
      </header>
      <main className="flex flex-1 flex-col items-center justify-center px-4">
        <h1 className="mb-6 text-center text-5xl font-bold tracking-tight">
          Organize your tasks with{" "}
          <span className="text-primary">NexusTasks</span>
        </h1>
        <p className="mb-8 max-w-2xl text-center text-xl text-muted-foreground">
          A modern task management platform built for teams and individuals who
          want to get things done.
        </p>
        <div className="flex gap-4">
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
      </main>
    </div>
  );
}
