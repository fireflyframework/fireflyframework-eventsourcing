/*
 * Copyright 2024-2026 Firefly Software Solutions Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fireflyframework.eventsourcing.tracing;

/**
 * @deprecated Tracing is now provided by {@code fireflyframework-observability} via
 * Micrometer Observation API with OpenTelemetry bridge. This class is intentionally
 * empty and will be removed in a future release.
 */
@Deprecated(since = "26.02.05", forRemoval = true)
public class OpenTelemetryConfiguration {
}
